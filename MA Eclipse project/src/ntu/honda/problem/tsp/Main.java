package ntu.honda.problem.tsp;

import java.io.IOException;

import csv.CsvWriter;


public class Main {

	/**
	 * @param args
	 */

	public static final int timeLimit = 10000;
	public static final int popSize = 10;
	public static final double mutProbability = 0.3;
	
	public static final String folderPath = "Instances/";
	
	
	public static void main(String[] args) {
		
		//new Main().simulationMain();
		new Main().showDemoMain();	
	}
	
	public void simulationMain() {
		String[] files = new String[] { "att48", "gr137", "pr152", "rat195" };
		TSPLocalSearch[] localSearchs = new TSPLocalSearch[] { TSPLocalSearch.None, TSPLocalSearch.First, TSPLocalSearch.Best };
		int[] popSizes = new int[] { 2, 10, 20 };
		double[] mutProbabilitys = new double[] { 0.3, 0.5, 0.7 };
		int times = 20;

		String avgOutfile = folderPath + "simulation.csv";
		CsvWriter writer = new CsvWriter(avgOutfile);
		try {
			writer.writeRecord(new String[] { "file", "timeLimit", "popSize", "mutProbability", "times", "localSearch", "avgBestFitness", "avgGeneration" });
			writer.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (String file : files) {
			for (TSPLocalSearch localSearch : localSearchs) {
				for (int m_popSize : popSizes) {
					for (double m_mutProbabilitys : mutProbabilitys) {

						String infile = folderPath + file + ".in";

						double totalBestFitness = 0;
						double totalGeneration = 0;
						for (int k = 1; k <= times; k++) {
							System.out.println(file+"-"+localSearch+"-"+m_popSize+"-"+m_mutProbabilitys+"-"+k);
							String outfile = folderPath + file + "-java-" + k + ".out";
							TSPInstance instance = new TSPInstance(infile, outfile);

							// Setting the parameters of the MA for this
							// instance of the problem.
							int[] lowerBounds = new int[instance.NumberCities];
							int[] upperBounds = new int[instance.NumberCities];
							for (int i = 0; i < instance.NumberCities; i++) {
								lowerBounds[i] = 0;
								upperBounds[i] = instance.NumberCities - 1;
							}

							TSPMemetic MA = new TSPMemetic(instance, localSearch, m_popSize, m_mutProbabilitys, lowerBounds, upperBounds);
							MA.Run(timeLimit);
							//instance.Write(MA.getBestFitness(), MA.getGeneration(), MA.getBestIndividual());
							totalBestFitness += MA.getBestFitness();
							totalGeneration += MA.getGeneration();
						}

						try {
							writer.write(file);
							writer.write(timeLimit);
							writer.write(m_popSize);
							writer.write(m_mutProbabilitys);
							writer.write(times);
							writer.write(localSearch.toString());
							writer.write((totalBestFitness / times));
							writer.write((totalGeneration / times));
							writer.endRecord();
							writer.flush();

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		writer.close();
	}
	
	public void showDemoMain() {
		
		String infile = folderPath + "att48.in";
		String outfile = folderPath + "att48-java.out";
		
		TSPInstance instance = new TSPInstance(infile, outfile);
		
		// Setting the parameters of the MA for this instance of the problem.
		int[] lowerBounds = new int[instance.NumberCities];
		int[] upperBounds = new int[instance.NumberCities];
		for (int i = 0; i < instance.NumberCities; i++) {
			lowerBounds[i] = 0;
			upperBounds[i] = instance.NumberCities - 1;
		}
		
		Demo demo = new Demo();
		demo.NodeNum = instance.NumberCities;
		
		TSPMemetic MA = new TSPMemetic(instance, TSPLocalSearch.First, popSize, mutProbability, lowerBounds, upperBounds);
		MA.demo = demo;
		
		MA.Run(timeLimit);
		instance.Write(MA.getBestFitness(), MA.getGeneration(), MA.getBestIndividual());
	}
	
	
	
}
