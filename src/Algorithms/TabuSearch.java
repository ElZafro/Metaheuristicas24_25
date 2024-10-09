package Algorithms;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import Utils.Printer;

public class TabuSearch implements Algorithm {

	final Random random;
	final Params params;
	final long initialTime;

	private final RandomGreedy greedy;

	private int dynamicVicinity;
	private Problem problem;

	private int[][] memory;

	public TabuSearch(Params params, long initialTime) {
		this.params = params;
		this.dynamicVicinity = (int) (this.params.dynamicVicinity / 100.0 * (float) this.params.maxIterations);
		this.random = new Random(params.seed);
		this.initialTime = initialTime;

		this.greedy = new RandomGreedy(new RandomGreedy.Params(params.seed, 5), initialTime);
	}

	@Override
	public Solution Solve(Problem problem) {

		this.memory = new int[problem.size][problem.size];

		var threshold = (int) (this.params.vicinitySliceFactor / 100.0 * (float) this.params.maxIterations);
		this.problem = problem;

		Solution best = this.greedy.Solve(problem);
		Solution currentBest = new Solution(best);
		Solution current = new Solution(currentBest);
		int worseMovements = 0;
		for (var it = 0; it < this.params.maxIterations; it++) {
			var vicinity = current.generateVicinity(this.random, this.problem, this.dynamicVicinity);

			current.apply(Arrays.stream(vicinity)
					.sorted((x, y) -> Double.compare(x.cost, y.cost))
					.findFirst()
					.get());

			if (current.cost >= currentBest.cost) {
				worseMovements++;
			} else {
				currentBest = new Solution(current);
			}

			if (worseMovements >= this.params.maxIterations * 5 / 100) {

				// TODO: Mostrar en log en vez de por consola
				// Printer.printlnDebug("Reinicializando por estancamiento");
				current = this.greedy.Solve(problem);
				worseMovements = 0;
			}

			if (currentBest.cost < best.cost) {
				best = new Solution(currentBest);
			}

			if (it % threshold == 0) {
				Printer.printlnDebug("Iteración " + it + "\t| Tamaño entorno: " + this.dynamicVicinity);
				this.dynamicVicinity = (this.dynamicVicinity * 90) / 100;
			}
		}

		return best;
	}

	private void updateMemory(Solution current) {

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
