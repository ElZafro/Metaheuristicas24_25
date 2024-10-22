package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import Algorithms.Algorithm.Solution;

public final class Logger {

	private static Logger instance;

	public static void init(String filepath) throws IOException {
		Logger.instance = new Logger(filepath);
	}

	public static void printMessage(String msg) {
		print("\t" + msg + "\n");
	}

	public static void printSolution(String name, Solution solution) {
		print("\t" + name + ":\n\t\t");
		printArray(solution.assignations);
		print("\n\t\tCoste: " + solution.cost + "\n");
	}

	/**
	 * Es necesario llamar a esta función al final de la ejecución del programa
	 */
	public static void close() {
		if (instance != null)
			instance.printer.close();
	}

	private static void printArray(int[] arr) {
		print("[ ");
		for (int i = 0; i < arr.length; ++i)
			print(arr[i] + " ");
		print("]");
	}

	private static void print(String message) {
		instance.printer.write(message);
	}

	private PrintWriter printer;

	private Logger(String filepath) throws IOException {

		File file = new File(filepath);
		file.getParentFile().mkdirs();
		this.printer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	}
}