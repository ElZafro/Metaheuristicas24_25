package Algorithms;

public interface Algorithm {

	public abstract Solution Solve(Problem problem);

	public final class Solution {
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
	}

}