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
import Utils.Logger;
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
		this.dynamicVicinity = (int) (this.params.initialVicinity / 100.0 * (float) this.params.maxIterations);
		this.stagnation = (int) (this.params.stagnation / 100.0 * (float) this.params.maxIterations);
		this.random = new Random(params.seed);
		this.initialTime = initialTime;

		this.greedy = new RandomGreedy(new RandomGreedy.Params(params.seed, 5), initialTime);
	}

	@Override
	public Solution Solve(Problem problem) {

		Logger.printMessage(this.params.toString());

		this.longMemory = new int[problem.size][problem.size];
		this.shortMemory = new CircularArray<>(2 * this.params.tabuTenure);

		var threshold = (int) (this.params.iterationsToDecreaseVicinity / 100.0 * (float) this.params.maxIterations);
		this.problem = problem;

		Solution best = this.greedy.Solve(problem);
		Solution currentBest = new Solution(best);
		Solution current = new Solution(currentBest);
		Logger.printSolution("Solución Inicial", current);
		int worsenMovements = 0;

		for (var it = 0; it < this.params.maxIterations; it++) {
			var vicinity = current.generateVicinity(this.random, this.problem, this.dynamicVicinity);

			if (it % threshold == 0) {
				Logger.printMessage("Tamaño entorno: " + this.dynamicVicinity);
				Printer.printlnDebug(
						"Iteración " + it + "\t| Tamaño entorno: " + this.dynamicVicinity + "\t| Óptimo: " + best.cost);
				this.dynamicVicinity = (this.dynamicVicinity * (int) (100.0 - this.params.vicinitySliceFactor)) / 100;
			}

			var neighbour = Arrays.stream(vicinity)
					.sorted((x, y) -> Double.compare(x.cost, y.cost))
					.filter(x -> !this.shortMemory.contains(x.swaps))
					.findFirst()
					.get();

			updateShortTerm(current, neighbour.swaps);
			current.apply(neighbour);
			Logger.printSolution("Iteración " + (it + 1), current);
			updateLongTerm(current);

			if (current.cost >= currentBest.cost) {
				worsenMovements++;
			} else {
				currentBest = new Solution(current);
				Logger.printMessage("Nuevo Mejor Del Momento");
				if (currentBest.cost < best.cost) {
					best = new Solution(currentBest);
					Logger.printMessage("Nuevo Mejor Global");
				}
			}

			if (worsenMovements >= this.stagnation) {
				current = this.reinitialize();
				currentBest = new Solution(current);
				Logger.printMessage("Nuevo Mejor Del Momento");
				worsenMovements = 0;
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
		Logger.printMessage("Reinicialización con memoria a largo plazo (" + strat + ")");
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
		public final float initialVicinity;
		public final float iterationsToDecreaseVicinity;
		public final int tabuTenure;
		public final float oscillation;
		public final float stagnation;

		public String toString() {
			return """
					Configuración
							Semilla: %s
							Número de iteraciones: %s
							Tamaño del entorno inicial: %s%% del número de iteraciones
							Disminución del entorno: Reducir un %s%% de su tamaño
							Iteraciones para la disminución: Cada %s%% del número de iteraciones
							Estancamiento: %s%% del número de iteraciones
							Tenencia tabú: %s
							Oscilación estratégica: %s%%
						""".formatted(seed, maxIterations, initialVicinity, vicinitySliceFactor,
					iterationsToDecreaseVicinity, stagnation, tabuTenure, oscillation);
		}

		public Params(Map<String, String> properties) throws Exception {
			try {
				this.seed = Integer.parseInt(properties.get("semilla"));
				this.maxIterations = Integer.parseInt(properties.get("maxIteraciones"));
				this.initialVicinity = Float.parseFloat(properties.get("entornoInicial"));
				this.vicinitySliceFactor = Float.parseFloat(properties.get("reduccionVecindad"));
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
