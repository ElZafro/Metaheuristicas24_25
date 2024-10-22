package Utils;

import java.util.Arrays;
import java.util.stream.IntStream;

import Algorithms.Algorithm.Solution;

public abstract class Printer {
	private static final String RED = "\u001B[31m";
	private static final String PURPLE = "\u001B[35m";
	private static final String RESET = "\u001B[0m";

	private static final int SOLUTION_DISPLAY_SIZE = 16;

	public static void printError(String err) {
		System.err.println(RED + "[ERROR]: " + RESET + err);
	}

	public static void printlnDebug(String msg) {
		System.out.println(PURPLE + "[DEBUG]: " + RESET + msg);
	}

	public static void printSolution(String name, Solution individual) {

		System.out.print(name + ":\n\t");
		if (individual.assignations.length <= 2 * SOLUTION_DISPLAY_SIZE)
			printFullArray(individual);
		else
			printSummarizedArray(individual);

		System.out.println("Coste: " + String.format("%.03f", individual.cost));
	}

	private static void printSummarizedArray(Solution individual) {
		var arr = individual.assignations;
		int[] part1 = Arrays.stream(arr).limit(SOLUTION_DISPLAY_SIZE).toArray();
		int[] part2 = IntStream.range(arr.length - SOLUTION_DISPLAY_SIZE, arr.length).map(i -> arr[i]).toArray();

		System.out.print("[ ");
		printArray(part1);
		System.out.print("... ");
		printArray(part2);
		System.out.print("]\n\t");

	}

	private static void printFullArray(Solution individual) {
		System.out.print("[ ");
		printArray(individual.assignations);
		System.out.print("]\n\t");
	}

	public static void printExecutionTime(long startTime) {
		System.out.format("Tiempo de ejecución: %.3fs\n\n", (System.currentTimeMillis() - startTime) * 1e-3);
	}

	private static void printArray(int[] arr) {
		for (int i = 0; i < arr.length; ++i)
			System.out.print(arr[i] + " ");
	}
}
