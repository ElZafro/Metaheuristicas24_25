public class App {

    public static void main(String[] args) throws Exception {
        Problem problem = new Problem(args[0]);
        Greedy solver = new Greedy();
        Algorithm.Solution solution = solver.Solve(problem);
        System.out.println(solution.value);
    }
}
