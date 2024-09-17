import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class Problem {
    ArrayList<ArrayList<Integer>> flowMatrix;
    ArrayList<ArrayList<Integer>> distanceMatrix;

    public Problem(String filepath) {

        this.flowMatrix = new ArrayList<ArrayList<Integer>>();
        this.distanceMatrix = new ArrayList<ArrayList<Integer>>();
        try {
            Scanner input = new Scanner(new File(filepath));
            int size = input.nextInt();
            input.nextLine();
            input.nextLine();

            for (int i = 0; i < size; ++i) {
                Scanner colReader = new Scanner(input.nextLine());
                ArrayList<Integer> col = new ArrayList<Integer>();
                while (colReader.hasNextInt()) {
                    col.add(colReader.nextInt());
                }
                this.flowMatrix.add(col);
            }

            input.nextLine();

            for (int i = 0; i < size; ++i) {
                Scanner colReader = new Scanner(input.nextLine());
                ArrayList<Integer> col = new ArrayList<Integer>();
                while (colReader.hasNextInt()) {
                    col.add(colReader.nextInt());
                }
                this.distanceMatrix.add(col);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }
}
