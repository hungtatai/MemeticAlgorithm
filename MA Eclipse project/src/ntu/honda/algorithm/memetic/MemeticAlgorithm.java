package ntu.honda.algorithm.memetic;

import java.util.ArrayList;

import javax.swing.JFrame;

import ntu.honda.algorithm.memetic.util.ArrayUtil;
import ntu.honda.algorithm.memetic.util.KeyValuePair;
import ntu.honda.algorithm.memetic.util.SortUtil;
import ntu.honda.algorithm.memetic.util.Statistics;
import ntu.honda.problem.tsp.Demo;

public abstract class MemeticAlgorithm {
	protected int PopulationSize;
	protected int[] LowerBounds ;
	protected int[] UpperBounds ;
	protected boolean RepairEnabled ;
	protected boolean LocalSearchEnabled;
	protected double MutationProbability ;
	
	protected Integer[] BestIndividual ;
	protected Double BestFitness ;
	protected int Generation;
	
	

	public MemeticAlgorithm (int popSize, double mutationProbability, int[] lowerBounds, int[] upperBounds)
	{
		PopulationSize = popSize + (popSize % 2);
		LowerBounds = lowerBounds;
		UpperBounds = upperBounds;
		RepairEnabled = false;
		LocalSearchEnabled = false;
		BestIndividual = null;
		BestFitness = 0.0;
		MutationProbability = mutationProbability;
		Generation = 0;
	}
	
	// Generate the initial meme.
	protected Boolean[] InitialMeme()
	{
		Boolean[] meme = ArrayUtil.newBooleanArray(LowerBounds.length);
		int points = Statistics.RandomDiscreteUniform(LowerBounds.length / 3, (2*LowerBounds.length) / 3);
		for (int i = 0; i < points; i++) {
			meme[Statistics.RandomDiscreteUniform(0, LowerBounds.length-1)] = true;
		}
		return meme;
	}
	
	// Generate the initial solution.
	protected abstract Integer[] InitialSolution();
	
	// Evaluate an individual of the population.
	protected abstract double Fitness(Integer[] key);
	
	// Repairing method to handle constraints.
	protected abstract void Repair(Integer[] individual);
	
	// Local search method.
	protected abstract void LocalSearch(Integer[] individual);
	
	

	public Demo demo = null;

	
	
	public void Run(int timeLimit)
	{	
		long startTime = System.currentTimeMillis();
		long iterationStartTime = 0;
		long iterationTime = 0;
		long maxIterationTime = 0;		
		long accumulationTime = 0;
		int numVariables = LowerBounds.length;
		ArrayList<KeyValuePair<Integer[], Boolean[]>> population =  new ArrayList<KeyValuePair<Integer[], Boolean[]>>(PopulationSize);
		ArrayList<Double> evaluation = new ArrayList<Double>(PopulationSize);
		
		int parent1 = 0;
		int parent2 = 0;
		KeyValuePair<Integer[], Boolean[]> descend = new KeyValuePair<Integer[], Boolean[]>(null,null);
		ArrayList<KeyValuePair<Integer[], Boolean[]>> iterationPopulation = new ArrayList<KeyValuePair<Integer[], Boolean[]>>(PopulationSize);
		ArrayList<Double> iterationEvaluation = new ArrayList<Double>(PopulationSize);
		ArrayList<KeyValuePair<Integer[], Boolean[]>> newPopulation = null;
		ArrayList<Double> newEvaluation = null;
		
		// Generate the initial random population.
		population.clear();
		for (int k = 0; k < PopulationSize; k++) {
			population.add(new KeyValuePair<Integer[], Boolean[]>(this.InitialSolution(),this.InitialMeme()));
		}
		
		// Run a local search method for each individual in the population.
		if (LocalSearchEnabled) {
			for (int k = 0; k < PopulationSize; k++) {
				LocalSearch(population.get(k).Key);
			}
		}				
		
		// Evaluate the population.
		evaluation.clear();
		for (int k = 0; k < PopulationSize; k++) {
			evaluation.add(Fitness(population.get(k).Key));
		}
		SortUtil.sort(evaluation, population);
		
		BestIndividual = population.get(0).Key;
		BestFitness = evaluation.get(0);
		
		
		if(demo != null) {
    	
			demo.init();
			demo.start();
			demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	demo.pack();
	    	demo.setVisible(true);
	    	
		}
				
		maxIterationTime = System.currentTimeMillis() - startTime;				
		//while (System.currentTimeMillis() - startTime < timeLimit - maxIterationTime) {
		while(accumulationTime < timeLimit - maxIterationTime) {
			iterationStartTime = System.currentTimeMillis();
			newPopulation = new ArrayList<KeyValuePair<Integer[], Boolean[]>>(PopulationSize);
			newEvaluation = new ArrayList<Double>(PopulationSize);

			// Apply the selection method.
			if (BestIndividual == null || evaluation.get(0) < BestFitness) {
				BestIndividual = population.get(0).Key;
				BestFitness = evaluation.get(0);
			}
			
			// Mutation points.
			int mut1stPoint = Statistics.RandomDiscreteUniform(0, numVariables - 1);		
			int mut2ndPoint = Statistics.RandomDiscreteUniform(0, numVariables - 1);		
			 
			iterationPopulation.clear();
			for (int i = 0; i < PopulationSize; i++) {
				// Selection (four individual tournament).
				parent1 = Math.min(Math.min(Statistics.RandomDiscreteUniform(0,PopulationSize-1), Statistics.RandomDiscreteUniform(0,PopulationSize-1)),
                                  	Math.min(Statistics.RandomDiscreteUniform(0,PopulationSize-1), Statistics.RandomDiscreteUniform(0,PopulationSize-1)));
				parent2 = Math.min(Math.min(Statistics.RandomDiscreteUniform(0,PopulationSize-1), Statistics.RandomDiscreteUniform(0,PopulationSize-1)),
                        			Math.min(Statistics.RandomDiscreteUniform(0,PopulationSize-1), Statistics.RandomDiscreteUniform(0,PopulationSize-1)));
				if (parent1 > parent2) {
					int tmp = parent1;
					parent1 = parent2;
					parent2 = tmp;
				}
				// Crossover with the meme of the best parent.
				descend = new KeyValuePair<Integer[], Boolean[]>(ArrayUtil.newIntegerArray(numVariables), population.get(parent1).Value);
				for (int j = 0; j < numVariables; j++) {
					if (descend.Value[j]) {
						descend.Key[j] = population.get(parent1).Key[j];
					}
					else {
						descend.Key[j] = population.get(parent2).Key[j]; //?
					}
				}
				
				// Mutation.
				if (Statistics.RandomUniform() < MutationProbability) {
					descend.Key[mut1stPoint] = Statistics.RandomDiscreteUniform(LowerBounds[mut1stPoint], UpperBounds[mut1stPoint]);									
					descend.Key[mut2ndPoint] = Statistics.RandomDiscreteUniform(LowerBounds[mut2ndPoint], UpperBounds[mut2ndPoint]);									
				}
				iterationPopulation.add(descend);
			}
			
			// Handle constraints using a repairing method.
			if (RepairEnabled) {
				for (int k = 0; k < PopulationSize; k++) {
					Repair(iterationPopulation.get(k).Key);
				}
			}
			
			// Run a local search method for each individual in the population.
			if (LocalSearchEnabled && 
			    System.currentTimeMillis() - startTime < timeLimit) {
				for (int k = 0; k < PopulationSize; k++) {
					LocalSearch(iterationPopulation.get(k).Key);
				}
			}				
			
			// Evaluate the population.
			iterationEvaluation.clear();
			for (int k = 0; k < PopulationSize; k++) {
				iterationEvaluation.add(Fitness(iterationPopulation.get(k).Key));
			}
			SortUtil.sort(iterationEvaluation, iterationPopulation);
			
			// Merge the new populations.
			int iterationIndex = 0;
			int existingIndex = 0;
			newPopulation.clear();
			newEvaluation.clear();
			for (int k = 0; k < PopulationSize; k++) {
				if (evaluation.get(existingIndex) < iterationEvaluation.get(iterationIndex)) {
					newPopulation.add(population.get(existingIndex));
					newEvaluation.add(evaluation.get(existingIndex));
					existingIndex++;
				}
				else {
					newPopulation.add(iterationPopulation.get(iterationIndex));
					newEvaluation.add(iterationEvaluation.get(iterationIndex));
					iterationIndex++;
				}
			}
			
			population = newPopulation;
			evaluation = newEvaluation;
			
			iterationTime = System.currentTimeMillis() - iterationStartTime;
			maxIterationTime = (maxIterationTime < iterationTime) ? iterationTime : maxIterationTime;
			accumulationTime += iterationTime;
			
			try {
				if(demo != null) {
					demo.bestPath = this.getBestIndividual();
					demo.bestCost = this.getBestFitness();
					demo.generation = Generation;
					
					Thread.sleep(demo.SleepTime*3);
					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Generation++;
		}
		if(demo != null) {
			demo.done = true;
		}
	}

	public int getPopulationSize() {
		return PopulationSize;
	}

	public int[] getLowerBounds() {
		return LowerBounds;
	}

	public int[] getUpperBounds() {
		return UpperBounds;
	}

	public boolean isRepairEnabled() {
		return RepairEnabled;
	}

	public boolean isLocalSearchEnabled() {
		return LocalSearchEnabled;
	}

	public double getMutationProbability() {
		return MutationProbability;
	}

	public Integer[] getBestIndividual() {
		return BestIndividual;
	}

	public Double getBestFitness() {
		return BestFitness;
	}
	
	public int getGeneration() {
		return Generation;
	}
}
