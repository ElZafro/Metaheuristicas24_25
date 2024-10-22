package Algorithms;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import Utils.*;

public class LocalSearch implements Algorithm {

	final Random random;
	final Params params;
	final long initialTime;

	private final RandomGreedy greedy;

	private int dynamicVicinity;
	private Problem problem;

	public LocalSearch(Params params, long initialTime) {
		this.params = params;
		this.dynamicVicinity = (int) (this.params.initialVicinity / 100.0 * (float) this.params.maxIterations);
		this.random = new Random(params.seed);
		this.initialTime = initialTime;

		this.greedy = new RandomGreedy(new RandomGreedy.Params(params.seed, 5), initialTime);
	}

	@Override
	public Solution Solve(Problem problem) {

		Logger.printMessage(this.params.toString());

		this.problem = problem;
		var threshold = (int) (this.params.iterationsToDecreaseVicinity / 100.0 * (float) this.params.maxIterations);

		Logger.printMessage("\nGreedy para solución inicial: ");
		Solution current = this.greedy.Solve(problem);

		Logger.printMessage("\nEjecución de Búsqueda Local");
		Logger.printMessage("Tamaño inicial del entorno: " + this.dynamicVicinity + "\t| Óptimo: " + current.cost);
		for (var it = 0; it < this.params.maxIterations; it++) {
			if (it > 0 && it % threshold == 0) {
				this.dynamicVicinity = (this.dynamicVicinity * (int) (100.0 - this.params.vicinitySliceFactor)) / 100;
				Logger.printMessage("Tamaño entorno: " + this.dynamicVicinity);
				Printer.printlnDebug("Iteración " + it + "\t| Tamaño entorno: " + this.dynamicVicinity +
						"\t| Óptimo: " + current.cost);
			}

			final var currentCost = current.cost;
			var vicinity = current.generateVicinity(this.random, this.problem, this.dynamicVicinity);
			var bestNeighbour = Arrays.stream(vicinity).filter(x -> x.cost < currentCost).findFirst();

			if (bestNeighbour.isEmpty()) {
				Printer.printlnDebug("Iteraciones realizadas: " + it);
				return current;
			} else {
				current.apply(bestNeighbour.get());
			}

			Logger.printSolution("Iteración " + (it + 1), current);
		}

		return current;
	}

	public static class Params {
		public final int seed;
		public final int maxIterations;
		public final float vicinitySliceFactor;
		public final float initialVicinity;
		public final float iterationsToDecreaseVicinity;

		public String toString() {
			return """
					Configuración
							Semilla: %s
							Número de iteraciones: %s
							Tamaño del entorno inicial: %s%% del número de iteraciones
							Disminución del entorno: Reducir un %s%% de su tamaño
							Iteraciones para la disminución: Cada %s%% del número de iteraciones
						""".formatted(seed, maxIterations, initialVicinity,
					vicinitySliceFactor, iterationsToDecreaseVicinity);
		}

		public Params(Map<String, String> properties) throws Exception {
			try {
				this.seed = Integer.parseInt(properties.get("semilla"));
				this.maxIterations = Integer.parseInt(properties.get("maxIteraciones"));
				this.vicinitySliceFactor = Float.parseFloat(properties.get("reduccionVecindad"));
				this.initialVicinity = Float.parseFloat(properties.get("entornoInicial"));
				this.iterationsToDecreaseVicinity = Float.parseFloat(properties.get("numIteracionesReduccionVecindad"));
			} catch (Exception e) {
				throw new Exception("Faltan parámetros en el archivo de configuración para el algoritmo LocalSearch");
			}
		}
	}
}
