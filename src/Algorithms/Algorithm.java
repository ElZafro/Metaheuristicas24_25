package Algorithms;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import Utils.Array;
import Utils.IntPair;

public interface Algorithm {

	public abstract Solution Solve(Problem problem);

	public static final class Solution {
		public int[] assignations;
		public double cost;

		public Solution(int size) {
			this.assignations = new int[size];
			this.cost = Float.MAX_VALUE;
		}

		public Solution(Solution other) {
			this.assignations = other.assignations.clone();
			this.cost = other.cost;
		}

		/**
		 * Modifica la solución "in-place" para moverse a un vecino.
		 * Esto permite tener 0 reservas de memoria efectivas a lo largo de la ejecución
		 * del programa. (Excepto la primera)
		 */
		public void apply(Neighbour neighbour) {
			Array.Swap(this.assignations, neighbour.swaps.first(), neighbour.swaps.second());
			this.cost = neighbour.cost;
		}

		/**
		 * En vez de generar soluciones completas explorando el entorno,
		 * es más eficiente devolver sólo los movimientos + coste. De esta forma,
		 * evitamos
		 * reservar memoria para Soluciones que se descartan inmediatamente.
		 * 
		 * @return *size* vecinos aleatorios
		 */
		public Neighbour[] generateVicinity(Random random, Problem p, int size) {
			// Es necesario generar los números aleatorios en un único hilo para que el
			// resultado sea determinista, luego pueden ser distribuidos en otros hilos.
			IntPair[] movements = IntStream.range(0, size)
					.mapToObj(x -> new IntPair(
							random.nextInt(this.assignations.length),
							random.nextInt(this.assignations.length)))
					.toArray(IntPair[]::new);

			return Arrays.stream(movements)
					// Hacerlo en paralelo resulta en peores tiempos de ejecución
					// .parallel()
					.map(x -> p.TwoOpt(this, x))
					.toArray(Neighbour[]::new);
		}
	}

	public static final class Neighbour {
		public final IntPair swaps;
		public final double cost;

		public Neighbour(IntPair swaps, double cost) {
			this.swaps = swaps;
			this.cost = cost;
		}
	}

}