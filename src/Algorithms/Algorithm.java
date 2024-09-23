package Algorithms;

public interface Algorithm {

	public abstract Solution Solve(Problem problem);

	public final class Solution {
		public int[] assignations;
		public float cost;

		public Solution(int size) {
			this.assignations = new int[size];
			this.cost = Float.MAX_VALUE;
		}
	}
}