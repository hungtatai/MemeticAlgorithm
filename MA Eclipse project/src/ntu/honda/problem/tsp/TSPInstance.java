package ntu.honda.problem.tsp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TSPInstance {

	public Integer NumberCities;
	public double[][] Costs;
	

	private String OutputFilePath;
	
	public TSPInstance(String inputFilePath, String outputFilePath) {
		Read(inputFilePath);
		this.OutputFilePath = outputFilePath;
	}
	
	public void Write(Double bestCost, Integer generation, Integer[] bestPath) {
		this.Write(this.OutputFilePath, bestCost, generation, bestPath);
	}
	
	private void Write(String filepath, Double bestCost, Integer generation, Integer[] bestPath) {
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
			writer.write("BestCost: "+bestCost.toString());
			writer.newLine();
			writer.write("Generation: "+generation.toString());
			writer.newLine();
			writer.write("NumberCities: "+NumberCities.toString());
			writer.newLine();
			
			for (int i = 0; i < NumberCities; i++) {
				writer.write(String.valueOf(bestPath[i] + 1));
				writer.newLine();				
			}
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void Read(String filepath) {
		double[] xCoords = null, yCoords = null;
		try {
			Scanner reader = new Scanner(new File(filepath));
			String line = "";
			
			// Getting the dimension.
			NumberCities = -1;
			while(NumberCities == -1) {
				line = reader.nextLine();
				if(line.startsWith("DIMENSION")) {
					NumberCities = Integer.parseInt(line.substring(11).trim());
					xCoords = new double[NumberCities];
					yCoords = new double[NumberCities];
					Costs = new double[NumberCities][NumberCities];
				}
			}
			
			// Getting the coordinates of the cities.
			while(!line.startsWith("NODE_COORD_SECTION")) {
				line = reader.nextLine();
			}
			for (int k = 0; k < NumberCities; k++) {
				line = reader.nextLine();
				String[] parts = line.trim().split(" +");
				int i = Integer.parseInt(parts[0]) - 1;
				xCoords[i] = Double.parseDouble(parts[1]);
				yCoords[i] = Double.parseDouble(parts[2]);
			}
			

			// Building the matrix of distances.
			for (int i = 0; i < NumberCities; i++) {
				for (int j = 0; j < NumberCities; j++) {
					Costs[i][j] = Math.sqrt(Math.pow(xCoords[i] - xCoords[j], 2) + 
					                        Math.pow(yCoords[i] - yCoords[j], 2));
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
