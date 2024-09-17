import java.util.ArrayList;
import java.util.Collections;

public class Greedy implements Algorithm {

    public Solution Solve(Problem problem) {

        Solution solution = new Solution();

        solution.assignations = new ArrayList<Integer>();
        ArrayList<Integer> flow_sum = new ArrayList<Integer>();
        ArrayList<Integer> distance_sum = new ArrayList<Integer>();

        // flow_sum[i] = suma de la fila entera
        for (int i = 0; i < problem.distanceMatrix.size(); ++i) {
            int flow = 0;
            int distance = 0;
            for (int j = 0; j < problem.distanceMatrix.size(); ++j) {
                flow += problem.flowMatrix.get(i).get(j);
                distance += problem.distanceMatrix.get(i).get(j);
            }
            flow_sum.add(flow);
            distance_sum.add(distance);
            solution.assignations.add(-1);
        }

        for (int i = 0; i < problem.distanceMatrix.size(); ++i) {
            int index1 = flow_sum.indexOf(getMax(flow_sum));
            int index2 = distance_sum.indexOf(Collections.min(distance_sum));

            System.out.printf("Asignaciones: %s x %s\n", index2, index1);

            flow_sum.set(index1, Integer.MIN_VALUE);
            distance_sum.set(index2, Integer.MAX_VALUE);

            solution.assignations.set(index1, index2);
        }

        System.out.println(solution.assignations);

        for (int i = 0; i < problem.distanceMatrix.size(); ++i) {
            for (int j = 0; j < problem.distanceMatrix.size(); ++j) {

                solution.value += problem.flowMatrix.get(i).get(j)
                        * problem.distanceMatrix.get(solution.assignations.get(i)).get(solution.assignations.get(j));
            }
        }

        return solution;
    }

    static Integer getMax(ArrayList<Integer> arr) {
        Integer res = arr.get(0);
        for (int i = 1; i < arr.size(); ++i) {
            if (arr.get(i) >= res) {
                res = arr.get(i);
            }
        }

        return res;
    }
}
