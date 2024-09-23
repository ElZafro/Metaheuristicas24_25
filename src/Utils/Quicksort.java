package Utils;

import java.util.stream.IntStream;

public class Quicksort {
	private final int[] indices;
	private final float[] distances;

	/**
	 * Performs an in-place quicksort on the given array of distances and returns
	 * the sorted indices.
	 *
	 * @param distances An array of distances to be sorted.
	 * @return An array of indices representing the sorted order of the distances.
	 *
	 */
	public static int[] Sort(float[] distances) {
		int[] indices = IntStream.range(0, distances.length).toArray();
		Quicksort sq = new Quicksort(indices, distances);
		sq.quickSort(0, distances.length - 1);
		return indices;
	}

	Quicksort(int[] indices, float[] distances) {
		this.indices = indices;
		this.distances = distances;
	}

	void quickSort(int low, int high) {
		if (low < high) {
			int pi = partition(low, high);
			quickSort(low, pi - 1);
			quickSort(pi + 1, high);
		}
	}

	private int partition(int low, int high) {
		float pivot = distances[indices[high]];
		int i = low - 1;
		for (int j = low; j < high; j++) {
			if (distances[indices[j]] <= pivot) {
				i++;
				Array.Swap(indices, i, j);
			}
		}
		Array.Swap(indices, i + 1, high);
		return i + 1;
	}
}
