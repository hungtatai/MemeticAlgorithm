package ntu.honda.algorithm.memetic.util;

import java.util.Collection;
import java.util.Random;

public class Statistics {
	private static Random random = new Random();

	public static double RandomUniform() {
		return random.nextDouble();
	}

	public static double RandomUniform(double a, double b) {
		return a + (b - a) * random.nextDouble();
	}

	public static int RandomDiscreteUniform(int a, int b) {
		return (int) Math.floor(RandomUniform(a, b + 1));
	}

	public static int RandomPoisson(double lambda) {
		int k = 0;
		double p = 1.0;
		double L = Math.exp(-lambda);

		do {
			k++;
			p *= random.nextDouble();
		} while (p >= L);

		return k - 1;
	}

	public static double RandomExponential(double alpha) {
		return -Math.log(random.nextDouble()) / alpha;
	}

	public static double Mean(Collection<Double> sample) {
		double mean = 0;

		for (double item : sample) {
			mean += item;
		}
		mean /= sample.size();

		return mean;
	}

	public static double Variance(Collection<Double> sample) {
		return Variance(sample, Mean(sample));
	}

	public static double Variance(Collection<Double> sample, double mean) {
		double variance = 0;

		for (double item : sample) {
			variance += Math.pow(item - mean, 2);
		}
		variance /= (sample.size() - 1);

		return variance;
	}

	public static double StandardDeviation(Collection<Double> sample) {
		return StandardDeviation(sample, Mean(sample));
	}

	public static double StandardDeviation(Collection<Double> sample, double mean) {
		return Math.sqrt(Variance(sample, mean));
	}

	public static int SampleRoulette(Collection<Double> probabilities) {
		double accumulative = 0;
		int current = 0;
		int selected = probabilities.size() - 1;
		double u = RandomUniform();

		for (double probability : probabilities) {
			accumulative += probability;
			if (u <= accumulative) {
				selected = current;
				break;
			}
			current++;
		}

		return selected;
	}
}
