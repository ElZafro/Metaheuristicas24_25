package Algorithms;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import Utils.Array;
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
			var vicinity = generateVicinity(current, this.dynamicVicinity);
			for (Solution solution : vicinity) {
				if (solution.cost < current.cost) {
					current = solution;
				}
			}
			if (it % threshold == 0) {
				Printer.printlnDebug("Iteración " + it + "\t| Tamaño entorno: " + this.dynamicVicinity);
				this.dynamicVicinity = (this.dynamicVicinity * 90) / 100;
			}
		}

		return current;
	}

	private Solution[] generateVicinity(Solution current, int size) {
		// Solution[] vicinity = new Solution[size];

		// for (int i = 0; i < size; i++) {
		// vicinity[i] = TwoOpt(current,
		// this.random.nextInt(current.assignations.length),
		// this.random.nextInt(current.assignations.length));
		// }

		// return vicinity;

		// ------------------------------------------------------------------------------------
		// return IntStream.range(0, size)
		// .parallel()
		// .mapToObj(i -> TwoOpt(current,
		// this.random.nextInt(current.assignations.length),
		// this.random.nextInt(current.assignations.length)))
		// .toArray(Solution[]::new);
		// ------------------------------------------------------------------------------------

		// Es necesario generar los números aleatorios en un único hilo para que el
		// resultado sea determinista, luego pueden ser distribuidos en otros hilos.
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<Integer, Integer>[] movements = IntStream.range(0, size)
				.mapToObj(x -> new AbstractMap.SimpleEntry<Integer, Integer>(
						this.random.nextInt(current.assignations.length),
						this.random.nextInt(current.assignations.length)))
				.toArray(AbstractMap.SimpleEntry[]::new);

		// Hacerlo en paralelo es perjudicial para problemas pequeños, pero merece la
		// pena para los grandes
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
