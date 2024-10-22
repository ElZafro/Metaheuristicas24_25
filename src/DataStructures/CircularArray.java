package DataStructures;

import java.util.Arrays;
import java.util.Iterator;

public class CircularArray<T> implements Iterable<T> {
	private int insertionIndex;
	private final T[] array;

	// Necesario para crear un array utilizando Generics
	@SuppressWarnings("unchecked")
	public CircularArray(int capacity) {
		this.insertionIndex = 0;
		this.array = (T[]) new Object[capacity];
	}

	public int size() {
		return array.length;
	}

	public void add(T element) {
		array[insertionIndex] = element;
		insertionIndex = (insertionIndex + 1) % array.length;
	}

	public T get(int index) {
		return array[index];
	}

	public boolean contains(T element) {

		for (var p : this) {
			if (p.equals(element)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return Arrays.stream(this.array).filter(el -> el != null).iterator();
	}
}
