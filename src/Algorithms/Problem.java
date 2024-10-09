package Algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.IntStream;

import Algorithms.Algorithm.Neighbour;
import Algorithms.Algorithm.Solution;

import Utils.IntPair;
import Utils.Printer;

public class Problem {
	public final double[][] cities;
	public final double[][] distances;
	public final int size;

	public Problem(String path) throws FileNotFoundException {
		var reader = new Scanner(new File(path)).useLocale(Locale.US);

		while (!reader.hasNext("DIMENSION.*"))
			reader.nextLine();

		while (!reader.hasNextInt())
			reader.next();

		this.size = reader.nextInt();

		this.cities = new double[size][2];
		this.distances = new double[size][size];

		while (!reader.hasNextInt())
			reader.next();

		while (!reader.hasNext("EOF")) {
			int indexCity = reader.nextInt() - 1;
			cities[indexCity][0] = reader.nextDouble();
			cities[indexCity][1] = reader.nextDouble();
		}

		reader.close();

		Printer.printlnDebug("Rellenando matrices");
		IntStream.range(0, size).parallel().forEach(i -> {
			double firsty_city = cities[i][0];
			double second_city = cities[i][1];

			// distances[i][i] = Double.POSITIVE_INFINITY;
			distances[i][i] = 0;

			IntStream.range(i + 1, size).forEach(j -> {
				double distance = (double) Math.hypot(firsty_city - cities[j][0], second_city - cities[j][1]);
				distances[i][j] = distance;
			});
		});

		IntStream.range(0, size).parallel().forEach(i -> {
			for (int j = 0; j < i; j++)
				distances[i][j] = distances[j][i];
		});
	}

	private double calculateCostAfterSwap(Algorithm.Solution prevSolution, IntPair pair) {

		int i = pair.first();
		int j = pair.second();

		var s = prevSolution.assignations;
		var l = this.size;

		double costDiff = 0;

		int iPrev = (i + l - 1) % l;
		int iNext = (i + 1) % l;
		int jPrev = (j + l - 1) % l;
		int jNext = (j + 1) % l;

		costDiff -= this.distances[s[iPrev]][s[i]];
		costDiff -= this.distances[s[i]][s[iNext]];
		costDiff -= this.distances[s[jPrev]][s[j]];
		costDiff -= this.distances[s[j]][s[jNext]];

		costDiff += this.distances[s[iPrev]][s[j]];
		costDiff += this.distances[s[j]][s[iNext]];
		costDiff += this.distances[s[jPrev]][s[i]];
		costDiff += this.distances[s[i]][s[jNext]];

		if (iNext == j || jNext == i) {
			costDiff += this.distances[s[i]][s[j]];
			costDiff += this.distances[s[j]][s[i]];
		}

		return prevSolution.cost + costDiff;
	}

	public double calculateCost(int[] assignations) {
		double result = distances[assignations[assignations.length - 1]][assignations[0]];
		for (int i = 0; i < assignations.length - 1; i++)
			result += distances[assignations[i]][assignations[(i + 1)]];

		return result;
	}

	public Neighbour TwoOpt(Solution current, IntPair pair) {
		return new Neighbour(
				pair,
				this.calculateCostAfterSwap(current, pair));
	}

}
