package Algorithms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.Comparator;
import java.util.function.ToIntFunction;

import DataStructures.CircularArray;
import DataStructures.OrderedIntPair;
import Utils.Printer;

public class TabuSearch implements Algorithm {

	final Random random;
	final Params params;
	final long initialTime;

	private final RandomGreedy greedy;

	private int dynamicVicinity;
	private int stagnation;
	private Problem problem;

	// Por diseño, solo utilizamos la mitad de la matriz (j > i)
	// Esto significa que el arco (3-4) comparte celda con el (4-3)
	private int[][] longMemory;

	// Almacenamos movimientos e índices indistintivamente
	// Si el movimiento que cambia la posición 0 con la 1 es tabú,
	// También lo será cambiar la ciudad 0 por la 1.
	private CircularArray<OrderedIntPair> shortMemory;

	public TabuSearch(Params params, long initialTime) {
		this.params = params;
		this.dynamicVicinity = (int) (this.params.dynamicVicinity / 100.0 * (float) this.params.maxIterations);
		this.stagnation = (int) (this.params.stagnation / 100.0 * (float) this.params.maxIterations);
		this.random = new Random(params.seed);
		this.initialTime = initialTime;

		this.greedy = new RandomGreedy(new RandomGreedy.Params(params.seed, 5), initialTime);
	}

	@Override
	public Solution Solve(Problem problem) {

		this.longMemory = new int[problem.size][problem.size];
		this.shortMemory = new CircularArray<>(2 * this.params.tabuTenure);

		var threshold = (int) (this.params.iterationsToDecreaseVicinity / 100.0 * (float) this.params.maxIterations);
		this.problem = problem;

		Solution best = this.greedy.Solve(problem);
		Solution currentBest = new Solution(best);
		Solution current = new Solution(currentBest);
		int worsenMovements = 0;

		for (var it = 0; it < this.params.maxIterations; it++) {
			var vicinity = current.generateVicinity(this.random, this.problem, this.dynamicVicinity);

			var neighbour = Arrays.stream(vicinity)
					.sorted((x, y) -> Double.compare(x.cost, y.cost))
					.filter(x -> !this.shortMemory.contains(x.swaps))
					.findFirst()
					.get();

			updateShortTerm(current, neighbour.swaps);
			current.apply(neighbour);
			updateLongTerm(current);

			if (current.cost >= currentBest.cost) {
				worsenMovements++;
			} else {
				currentBest = new Solution(current);
				if (currentBest.cost < best.cost) {
					best = new Solution(currentBest);
				}
			}

			if (worsenMovements >= this.stagnation) {

				// TODO: Mostrar en log en vez de por consola
				Printer.printlnDebug("Reinicializando con memoria a largo plazo");
				current = this.reinitialize();
				worsenMovements = 0;
			}

			if (it % threshold == 0) {
				// TODO: Mostrar en log también
				Printer.printlnDebug("Iteración " + it + "\t| Tamaño entorno: " + this.dynamicVicinity);
				this.dynamicVicinity = (this.dynamicVicinity * (int) (100.0 - this.params.vicinitySliceFactor)) / 100;
			}
		}

		return best;
	}

	private enum GenerationStrategy {
		Diversification,
		Intensification
	}

	private Solution reinitialize() {
		return (random.nextFloat() * 100 < this.params.oscillation)
				? generateNewSolution(GenerationStrategy.Intensification)
				: generateNewSolution(GenerationStrategy.Diversification);
	}

	private Solution generateNewSolution(GenerationStrategy strat) {

		Solution solution = new Solution(this.problem.size);

		ToIntFunction<OrderedIntPair> pairValueFunction = pair -> this.longMemory[pair.second()][pair.first()];
		Comparator<OrderedIntPair> comparator = (strat == GenerationStrategy.Intensification)
				? Comparator.comparingInt(pairValueFunction)
				: Comparator.comparingInt(pairValueFunction).reversed();

		var initial = IntStream.range(0, problem.size)
				.boxed()
				.flatMap(i -> IntStream.range(0, i).mapToObj(j -> new OrderedIntPair(i, j)))
				.max(comparator)
				.orElse(new OrderedIntPair(0, 1));
		solution.assignations[0] = initial.second();
		solution.assignations[1] = initial.first();

		Set<Integer> excludedCities = new HashSet<>(problem.size);
		excludedCities.add(solution.assignations[0]);

		for (int i = 1; i < problem.size - 1; ++i) {
			excludedCities.add(solution.assignations[i]);
			int lastCity = solution.assignations[i];
			int nextCity = findNextCity(lastCity, excludedCities, comparator);
			solution.assignations[i + 1] = nextCity;
		}

		solution.cost = this.problem.calculateCost(solution.assignations);
		return solution;
	}

	private int findNextCity(int lastCity, Set<Integer> excludedCities, Comparator<OrderedIntPair> comparator) {
		return IntStream.range(0, this.problem.size)
				.filter(x -> !excludedCities.contains(x))
				.filter(x -> this.longMemory[Math.max(x, lastCity)][Math.min(x, lastCity)] != 0)
				.boxed()
				.max(Comparator.comparing(x -> new OrderedIntPair(Math.max(x, lastCity), Math.min(x, lastCity)),
						comparator))
				.orElseGet(() -> IntStream.range(0, this.problem.size)
						.filter(x -> !excludedCities.contains(x))
						.findFirst()
						.getAsInt());
	}

	private void updateLongTerm(Solution current) {
		OrderedIntPair last = new OrderedIntPair(
				current.assignations[0],
				current.assignations[current.assignations.length - 1]);
		this.longMemory[last.second()][last.first()]++;

		for (int i = 0; i < current.assignations.length - 1; i++) {
			OrderedIntPair p = new OrderedIntPair(current.assignations[i], current.assignations[i + 1]);
			this.longMemory[p.second()][p.first()]++;
		}

	}

	private void updateShortTerm(Solution current, OrderedIntPair pair) {

		OrderedIntPair cities = new OrderedIntPair(
				current.assignations[pair.first()],
				current.assignations[pair.second()]);

		this.shortMemory.add(pair);
		this.shortMemory.add(cities);
	}

	public static class Params {
		public final int seed;
		public final int maxIterations;
		public final float vicinitySliceFactor;
		public final float dynamicVicinity;
		public final float iterationsToDecreaseVicinity;
		public final int tabuTenure;
		public final float oscillation;
		public final float stagnation;

		public Params(Map<String, String> properties) throws Exception {
			try {
				this.seed = Integer.parseInt(properties.get("semilla"));
				this.maxIterations = Integer.parseInt(properties.get("maxIteraciones"));
				this.vicinitySliceFactor = Float.parseFloat(properties.get("reduccionVecindad"));
				this.dynamicVicinity = Float.parseFloat(properties.get("entornoDinamico"));
				this.iterationsToDecreaseVicinity = Float.parseFloat(properties.get("numIteracionesReduccionVecindad"));
				this.tabuTenure = Integer.parseInt(properties.get("tenenciaTabu"));
				this.oscillation = Float.parseFloat(properties.get("oscilacionEstrategica"));
				this.stagnation = Float.parseFloat(properties.get("estancamiento"));
			} catch (Exception e) {
				throw new Exception("Faltan parámetros en el archivo de configuración para el algoritmo LocalSearch");
			}
		}
	}
}
