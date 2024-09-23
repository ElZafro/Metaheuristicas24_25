package Algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.IntStream;

import Utils.Printer;

public class Problem {
	public final float[][] cities;
	public final float[][] distances;
	public final int size;

	public final int[][] sortedCities;

	public Problem(String path) throws FileNotFoundException {
		var reader = new Scanner(new File(path)).useLocale(Locale.US);

		while (!reader.hasNext("DIMENSION.*"))
			reader.nextLine();

		while (!reader.hasNextInt())
			reader.next();

		this.size = reader.nextInt();

		this.cities = new float[size][2];
		this.distances = new float[size][size];

		while (!reader.hasNextInt())
			reader.next();

		while (!reader.hasNext("EOF")) {
			int indexCity = reader.nextInt() - 1;
			cities[indexCity][0] = reader.nextFloat();
			cities[indexCity][1] = reader.nextFloat();
		}

		reader.close();

		Printer.printlnDebug("Rellenando matrices");
		IntStream.range(0, size).parallel().forEach(i -> {
			float firsty_city = cities[i][0];
			float second_city = cities[i][1];

			distances[i][i] = Float.POSITIVE_INFINITY;

			IntStream.range(i + 1, size).forEach(j -> {
				float distance = (float) Math.hypot(firsty_city - cities[j][0], second_city - cities[j][1]);
				distances[i][j] = distance;
			});
		});

		IntStream.range(0, size).parallel().forEach(i -> {
			for (int j = 0; j < i; j++)
				distances[i][j] = distances[j][i];
		});

		sortedCities = new int[size][size];

		IntStream.range(0, size).parallel().forEach(i -> {
			final int referenceCity = i;
			final float[] distancesToReference = distances[referenceCity];

			final int[] sortedIndices = Utils.Quicksort.Sort(distancesToReference);

			sortedCities[i] = sortedIndices;
		});
	}

	public float calculateCost(int[] assignations) {
		float result = distances[assignations[assignations.length - 1]][assignations[0]];
		for (int i = 0; i < assignations.length - 1; i++)
			result += distances[assignations[i]][assignations[(i + 1)]];

		return result;
	}

}
