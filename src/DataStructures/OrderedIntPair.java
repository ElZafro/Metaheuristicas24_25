package DataStructures;

/**
 * first <= second
 */
public final record OrderedIntPair(int first, int second) {
	public OrderedIntPair {
		if (first > second) {
			int t = first;
			first = second;
			second = t;
		}
	}
}