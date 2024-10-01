import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Algorithms.RandomGreedy;
import Algorithms.Algorithm;
import Algorithms.LocalSearch;
import Algorithms.Algorithm.Solution;
import Algorithms.Problem;
import Utils.Printer;
// import Utils.Logger;

public class Config {

	static final String configFilePath = "./config.json";
	static final Pattern filepathPattern = Pattern.compile("\"archivo\":\\s*\"([^\"]*)\"");
	static final Pattern algorithmPattern = Pattern.compile("\"algoritmo\":\\s*\"([^\"]*)\"");
	static final Pattern logFilePattern = Pattern.compile("\"log\":\\s*\"([^\"]*)\"");
	static final Pattern propertiesPattern = Pattern
			.compile("\"propiedades\":\\s*\\{\\s*((\"[^\"]*\":\\s*[^,}\\s]+,?\\s*)+)\\}");

	final Problem problem;
	final Algorithm algorithm;

	final long startTime;

	public Config() throws Exception {

		startTime = System.currentTimeMillis();
		String config = loadConfig();

		String problemFilePath = readField(config, filepathPattern);

		try {
			this.problem = new Problem(problemFilePath);

		} catch (FileNotFoundException e) {
			throw new Exception("El archivo \"" + problemFilePath + "\" no se encontró");
		}

		var properties = readAlgorithmProperties(config);
		String algorithmType = readField(config, algorithmPattern);

		// String logFileName = createLogFileName(algorithmType, problemFilePath);
		// Logger.init(logFileName);

		this.algorithm = chooseAlgorithm(algorithmType, properties);

		Printer.printlnDebug("Ejecutando " + algorithmType + " sobre el problema " + problemFilePath + ".");
		// System.out.println("El archivo log se encuentra en " + logFileName);
	}

	public Solution Solve() {
		var solution = this.algorithm.Solve(this.problem);
		Printer.printExecutionTime(startTime);
		return solution;
	}

	private static String loadConfig() throws FileNotFoundException {
		Scanner sc = new Scanner(new File(configFilePath));
		StringBuilder sb = new StringBuilder();
		while (sc.hasNextLine())
			sb.append(sc.nextLine());
		sc.close();
		return sb.toString();
	}

	// private static String createLogFileName(String algorithmType, String
	// problemFilePath) {
	// String problemFile =
	// problemFilePath.substring(problemFilePath.lastIndexOf('/') + 1,
	// problemFilePath.lastIndexOf('.'));
	// String baseLogFileName = "./BIN/logs/" + algorithmType + "_" + problemFile +
	// "_";
	// int index = 0;
	// while (new File(baseLogFileName + index + ".txt").exists())
	// index++;
	// return baseLogFileName + index + ".txt";
	// }

	private Algorithm chooseAlgorithm(String algorithmType, HashMap<String, String> properties)
			throws Exception {

		switch (algorithmType) {
			case "RandomGreedy": {
				return new RandomGreedy(new RandomGreedy.Params(properties), startTime);
			}
			case "LocalSearch": {
				return new LocalSearch(new LocalSearch.Params(properties), startTime);
			}
			default:
				throw new Exception("El tipo de algoritmo " + algorithmType + " no está reconocido.");
		}

	}

	private String readField(String input, Pattern pattern) throws Exception {
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new Exception();
		}
	}

	private HashMap<String, String> readAlgorithmProperties(String input) throws Exception {

		var table = new HashMap<String, String>();
		Matcher matcher = propertiesPattern.matcher(input);

		if (matcher.find()) {
			String propertiesContent = matcher.group(1);
			String[] keyValuePairs = propertiesContent.split(",\\s*");

			for (String pair : keyValuePairs) {
				var entry = pair.split(":");
				var key = entry[0].trim().substring(1, entry[0].length() - 1);
				try {
					var value = entry[1].trim();
					table.put(key, value);
				} catch (Exception e) {
					throw new Exception("El valor de " + key + " está mal.");
				}
			}
		}

		return table;
	}

}