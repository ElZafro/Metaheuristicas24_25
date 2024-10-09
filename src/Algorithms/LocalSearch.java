package Algorithms;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import Utils.Printer;

public class LocalSearch implements Algorithm {

	final Random random;
	final Params params;
	final long initialTime;

	private final RandomGreedy greedy;

	private int dynamicVicinity;
	private Problem problem;

	public LocalSearch(Params params, long initialTime) {
		this.params = params;
		this.dynamicVicinity = (int) (this.params.dynamicVicinity / 100.0 * (float) this.params.maxIterations);
		this.random = new Random(params.seed);
		this.initialTime = initialTime;

		this.greedy = new RandomGreedy(new RandomGreedy.Params(params.seed, 5), initialTime);
	}

	@Override
	public Solution Solve(Problem problem) {

		var threshold = (int) (this.params.vicinitySliceFactor / 100.0 * (float) this.params.maxIterations);
		this.problem = problem;

		Solution current = this.greedy.Solve(problem);
		for (var it = 0; it < this.params.maxIterations; it++) {
			if (it % threshold == 0) {
				Printer.printlnDebug("Iteración " + it + "\t| Tamaño entorno: " + this.dynamicVicinity);
				this.dynamicVicinity = (this.dynamicVicinity * 90) / 100;
			}

			final var currentCost = current.cost;
			var vicinity = current.generateVicinity(this.random, this.problem, this.dynamicVicinity);
			var bestNeighbour = Arrays.stream(vicinity).filter(x -> x.cost < currentCost).findFirst();

			if (bestNeighbour.isEmpty()) {
				Printer.printlnDebug("Total iterations: " + it);
				return current;
			} else {
				current.apply(bestNeighbour.get());
			}
		}

		return current;
	}

	public static class Params {
		public final int seed;
		public final int maxIterations;
		public final float vicinitySliceFactor;
		public final float dynamicVicinity;

		public Params(Map<String, String> properties) throws Exception {
			try {
				this.seed = Integer.parseInt(properties.get("semilla"));
				this.maxIterations = Integer.parseInt(properties.get("maxIteraciones"));
				this.vicinitySliceFactor = Float.parseFloat(properties.get("reduccionVecindad"));
				this.dynamicVicinity = Float.parseFloat(properties.get("entornoDinamico"));
			} catch (Exception e) {
				throw new Exception("Faltan parámetros en el archivo de configuración para el algoritmo LocalSearch");
			}
		}
	}
}
