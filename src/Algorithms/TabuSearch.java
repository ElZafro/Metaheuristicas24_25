package Algorithms;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import Utils.Array;
import Utils.Printer;

public class TabuSearch implements Algorithm {

	final Random random;
	final Params params;
	final long initialTime;

	private final RandomGreedy greedy;

	private int dynamicVicinity;
	private Problem problem;

	public TabuSearch(Params params, long initialTime) {
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

		Solution best = this.greedy.Solve(problem);
		Solution currentBest = new Solution(best);
		Solution current = new Solution(currentBest);
		int worseMovements = 0;
		for (var it = 0; it < this.params.maxIterations; it++) {
			var vicinity = generateVicinity(current, this.dynamicVicinity);

			current = Arrays.stream(vicinity)
					.sorted((x, y) -> Double.compare(x.cost, y.cost))
					.findFirst()
					.get();

			if (current.cost >= currentBest.cost) {
				worseMovements++;
			} else {
				currentBest = new Solution(current);
			}

			if (worseMovements >= this.params.maxIterations * 5 / 100) {
				Printer.printlnDebug("Reinicializando por estancamiento");
				current = this.greedy.Solve(problem);
				worseMovements = 0;
			}

			if (currentBest.cost < best.cost) {
				best = new Solution(currentBest);
				Printer.printlnDebug("Nuevo mejor global: " + best.cost);
			}

			if (it % threshold == 0) {
				Printer.printlnDebug("Iteración " + it + "\t| Tamaño entorno: " + this.dynamicVicinity);
				this.dynamicVicinity = (this.dynamicVicinity * 90) / 100;
			}
		}

		return best;
	}

	private Solution[] generateVicinity(Solution current, int size) {
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<Integer, Integer>[] movements = IntStream.range(0, size)
				.mapToObj(x -> new AbstractMap.SimpleEntry<Integer, Integer>(
						this.random.nextInt(current.assignations.length),
						this.random.nextInt(current.assignations.length)))
				.toArray(AbstractMap.SimpleEntry[]::new);

		return Arrays.stream(movements)
				.parallel()
				.map(x -> TwoOpt(current, x.getKey(), x.getValue()))
				.toArray(Solution[]::new);
	}

	// TODO: La mayoría de soluciones se descartan, podríamos devolver el cambio +
	// nuevo coste, y solo cuando es elegido crear la nueva solución
	private Solution TwoOpt(Solution current, int i, int j) {
		var cloned = new Solution(current);
		Array.Swap(cloned.assignations, i, j);

		// cloned.cost = this.problem.calculateCost(cloned.assignations);
		cloned.cost = this.problem.calculateCostAfterSwap(current, i, j);

		return cloned;
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
