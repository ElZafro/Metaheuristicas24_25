import java.util.ArrayList;

public interface Algorithm {
    public static class Solution {
        ArrayList<Integer> assignations;
        int value;
    }

    public abstract Solution Solve(Problem problem);
}
