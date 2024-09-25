import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EjecucionPractica {
    /*
    private static int LIMIT = 5;

    private static long SEMILLA = 77688090;

    private final String[] nombresArchivos = {"a280.tsp", "ch130.tsp", "d18512.tsp", "pr144.tsp", "u1060.tsp"};
     */

    public EjecucionPractica() {}

    public void exec(){
        int LIMIT = 0;
        long SEMILLA = 0;
        try (FileReader fr = new FileReader("config.txt")) {
            BufferedReader br = new BufferedReader(fr);
            //Para el LIMIT
            String linea = br.readLine();
            LIMIT = Integer.parseInt(linea.split(" ")[2]);
            //Para la SEMILLA
            linea = br.readLine();
            SEMILLA = Long.parseLong(linea.split(" ")[2]);
            //Para el archivo
            linea = br.readLine();
            String nombreArchivo = linea.split(" ")[2];
            AlmacenarDatos.inicializacionDatos(nombreArchivo);

            //Para el algoritmo
            linea = br.readLine();
            long startTime = 0, endTime = 0;
            ArrayList<Integer> solucion = new ArrayList<>();
            if(linea.split(" ")[2].equals("GreedyAleatorio")){
                startTime = System.nanoTime();
                solucion = GreedyAleatorio.greedy(AlmacenarDatos.getDistancias().length, AlmacenarDatos.getDistancias(), LIMIT, SEMILLA);
                endTime = System.nanoTime();
            }
            long duration = endTime - startTime;

            double costeTotal = 0;
            for(int j = 0; j < (solucion.size() - 1); ++j){
                costeTotal += AlmacenarDatos.getDistancias()[solucion.get(j)][solucion.get(j + 1)];
            }
            System.out.println("Problema " + nombreArchivo + ":\nCoste de " + costeTotal);
            System.out.println("Tiempo de ejecución: " + (duration/1_000_000.0) + " milisegundos");
            System.out.println("Solucion: " + solucion);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}