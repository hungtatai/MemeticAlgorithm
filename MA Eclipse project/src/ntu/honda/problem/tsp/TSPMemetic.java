package ntu.honda.problem.tsp;

import java.util.ArrayList;
import java.util.List;

import ntu.honda.algorithm.memetic.MemeticAlgorithm;
import ntu.honda.algorithm.memetic.util.ArrayUtil;
import ntu.honda.algorithm.memetic.util.Statistics;

public class TSPMemetic extends MemeticAlgorithm{

	private TSPInstance Instance;
	private TSPLocalSearch LocalSearchType;
	private int generatedSolutions;
	
	public TSPMemetic(TSPInstance Instance, TSPLocalSearch localSearchType, 
			int popSize, double mutationProbability, int[] lowerBounds, int[] upperBounds) {
		super(popSize, mutationProbability, lowerBounds, upperBounds);
		this.Instance = Instance;
		this.RepairEnabled = true;
		this.LocalSearchEnabled = (localSearchType != TSPLocalSearch.None);
		this.LocalSearchType = localSearchType;
		this.generatedSolutions = 0;
	}

	@Override
	protected double Fitness(Integer[] individual) {
		
		double cost = 0;
		// In this, individual is path.
		Integer[] path = individual; 
		
		// Sum of path's cost.
		for(int i=1;i<path.length;i++){
			cost += Instance.Costs[path[i-1]][path[i]];
		}
		cost += Instance.Costs[path[path.length-1]][path[0]];

		return cost;
	}

	
	@Override
	protected Integer[] InitialSolution() {
		Integer[] solution;
		
		// First solution use greedy method.
		if(generatedSolutions == -1) {
			solution = this.GreedySolution();
		}
		else {
			solution = this.RandomSolution();
		}
		generatedSolutions++;
		
		return solution;
	}

	@Override
	protected void Repair(Integer[] individual) {
		int visitedCitiesCount = 0;
		boolean[] visitedCities = new boolean[Instance.NumberCities];
		boolean[] repeatedPositions = new boolean[Instance.NumberCities];
			
		// Get information to decide if the individual is valid.
		for (int i = 0; i < Instance.NumberCities; i++) {
			if (!visitedCities[individual[i]]) {
				visitedCitiesCount += 1;
				visitedCities[individual[i]] = true;
			}
			else {
				repeatedPositions[i] = true;
			}
		}
			
		// If the individual is invalid, make it valid.
		if (visitedCitiesCount != Instance.NumberCities) {
			for (int i = 0; i < repeatedPositions.length; i++) {
				if (repeatedPositions[i]) {
					int count = Statistics.RandomDiscreteUniform(1, Instance.NumberCities - visitedCitiesCount);
					for (int c = 0; c < visitedCities.length; c++) {
						if (!visitedCities[c]) {
							count -= 1;
							if (count == 0) {
								individual[i] = c;
								repeatedPositions[i] = false;
								visitedCities[c] = true;
								visitedCitiesCount += 1;
								break;
							}
						}
					}							
				}
			}
		}
		
	}

	@Override
	protected void LocalSearch(Integer[] individual) {
		if( this.LocalSearchType == TSPLocalSearch.First ) {
			this.LocalSearch2OptFirst(individual);
		} else if( this.LocalSearchType == TSPLocalSearch.Best ) {
			this.LocalSearch2OptBest(individual);
		} 
		
	}
	
	private Integer[] RandomSolution() {
		Integer[] solution = new Integer[Instance.NumberCities];
		List<Integer> cities = new ArrayList<Integer>();
		
		for(int city=0;city<Instance.NumberCities;city++)
			cities.add(city);
		
		for(int i=0;i<Instance.NumberCities;i++){
			// Random select a city from cities.
			int cityIndex = Statistics.RandomDiscreteUniform(0, cities.size()-1);
			int city = cities.get(cityIndex);
			
			// Append to solution.
			cities.remove(cityIndex);
			solution[i] = city;
		}
		
		return solution;
	}
	
	public Integer[] GreedySolution() {
		Integer[] solution = ArrayUtil.newIntegerArray(Instance.NumberCities);
		boolean[] visited = new boolean[Instance.NumberCities];
		
		for (int i = 0; i < Instance.NumberCities; i++) {
			if (i == 0) {
				solution[i] = 0;
			}
			else {
				int currentCity = solution[i-1];
				int nextCity;
				double bestCost = Double.MAX_VALUE;
				for (nextCity = 1; nextCity < Instance.NumberCities; nextCity++) {
                    if (!visited[nextCity] && Instance.Costs[currentCity][nextCity] < bestCost) {
						solution[i] = nextCity;
						bestCost = Instance.Costs[currentCity][nextCity];
					}
				}
			}
			visited[solution[i]] = true;
		}
		return solution;
	}
	
	private void LocalSearch2OptFirst(Integer[] path) {
		double currentFitness, bestFitness;

		bestFitness = this.Fitness(path);			
		for (int j = 1; j < path.length; j++) {
			for (int i = 0; i < j; i++) {
				// Swap the items.
				ArrayUtil.swap(path, j, i);
				
				// Evaluate the fitness of this new solution.
				currentFitness = this.Fitness(path);
				if (currentFitness < bestFitness) {
					return;
				}
				
				// Undo the swap.
				ArrayUtil.swap(path, j, i);
			}
		}
	}
	
	
	private void LocalSearch2OptBest(Integer[] path) {
		int firstSwapItem = 0, secondSwapItem = 0;
		double currentFitness, bestFitness;
		
		bestFitness = this.Fitness(path);			
		for (int j = 1; j < path.length; j++) {
			for (int i = 0; i < j; i++) {
				// Swap the items.
				ArrayUtil.swap(path, j, i);
				
				// Evaluate the fitness of this new solution.
				currentFitness = this.Fitness(path);
				if (currentFitness < bestFitness) {
					firstSwapItem = j;
					secondSwapItem = i;
					bestFitness = currentFitness;
				}
				
				// Undo the swap.
				ArrayUtil.swap(path, j, i);
			}
		}
		
		// Use the best assignment.
		if (firstSwapItem != secondSwapItem) {
			ArrayUtil.swap(path, firstSwapItem, secondSwapItem);
		}
	}

}
