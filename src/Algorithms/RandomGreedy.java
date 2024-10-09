package Algorithms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class RandomGreedy implements Algorithm {

	final Random random;
	final Params params;
	final long initialTime;

	public RandomGreedy(Params params, long initialTime) {
		this.params = params;
		this.random = new Random(params.seed);
		this.initialTime = initialTime;
	}

	@Override
	public Solution Solve(Problem problem) {
		Solution solution = new Solution(problem.size);
		Set<Integer> excludedCities = new HashSet<>(problem.size);

		double[] sumDistances = IntStream.range(0, problem.size)
				.parallel()
				.mapToDouble(x -> Arrays.stream(problem.distances[x]).sum())
				.toArray();

		int[] sortedCities = IntStream.range(0, problem.size)
				.parallel()
				.mapToObj(i -> new City(i, sumDistances[i]))
				.sorted((x, y) -> Double.compare(x.distance(), y.distance()))
				.mapToInt((x) -> x.index())
				.toArray();

		solution.assignations[0] = random.nextInt(sortedCities.length);

		for (int i = 0; i < problem.size - 1; i++) {
			excludedCities.add(solution.assignations[i]);
			int randomCity = getRandomCity(sortedCities, this.params.k, excludedCities);
			solution.assignations[i + 1] = randomCity;
		}

		solution.cost = problem.calculateCost(solution.assignations);

		return solution;
	}

	private int getRandomCity(int[] cities, int greedySize, Set<Integer> excludedSet) {
		var randomIndex = random.nextInt(Math.min(greedySize, cities.length - excludedSet.size()));
		return Arrays.stream(cities)
				.filter(city -> !excludedSet.contains(city))
				.skip(randomIndex)
				.findFirst()
				.getAsInt();
	}

	public static class Params {
		public final int seed;
		public final int k;

		public Params(int seed, int k) {
			this.seed = seed;
			this.k = k;
		}

		public Params(Map<String, String> properties) throws Exception {
			try {
				this.seed = Integer.parseInt(properties.get("semilla"));
				this.k = Integer.parseInt(properties.get("k"));
			} catch (Exception e) {
				throw new Exception("Faltan parámetros en el archivo de configuración para el algoritmo RandomGreedy");
			}
		}
	}

	private record City(int index, double distance) {
	}
}
