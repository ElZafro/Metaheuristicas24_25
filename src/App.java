import Algorithms.Algorithm.Solution;
import Utils.*;

public class App {
	public static void main(String[] args) {
		try {
			Config config = new Config();
			Solution solution = config.Solve();
			Printer.printSolution("Solución final", solution);
		} catch (Exception e) {
			Printer.printError(e.getMessage());
		} finally {
			Logger.close();
		}
	}
}
