import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EjecucionPractica {
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
            String nombreAlgoritmo = linea.split(" ")[2];
            if(nombreAlgoritmo.equals("GreedyAleatorio")){
                startTime = System.nanoTime();
                solucion = GreedyAleatorio.greedy(AlmacenarDatos.getDistancias().length, AlmacenarDatos.getDistancias(), LIMIT, SEMILLA);
                endTime = System.nanoTime();
            } else{
                if(nombreAlgoritmo.equals("LocalSearch")){
                    solucion = GreedyAleatorio.greedy(AlmacenarDatos.getDistancias().length, AlmacenarDatos.getDistancias(), LIMIT, SEMILLA);
                    linea = br.readLine();
                    int TOTAL_ITERACIONES = Integer.parseInt(linea.split(" ")[2]);
                    linea = br.readLine();
                    int ENTORNO_INICIAL = Integer.parseInt(linea.split(" ")[2]);
                    linea = br.readLine();
                    int PORCENTAJE_CAMBIO = Integer.parseInt(linea.split(" ")[2]);
                    linea = br.readLine();
                    int REDUCCION_ENTORNO = Integer.parseInt(linea.split(" ")[2]);
                    startTime = System.nanoTime();
                    solucion = LocalSearch.busquedalocal(solucion, TOTAL_ITERACIONES, ENTORNO_INICIAL, PORCENTAJE_CAMBIO, REDUCCION_ENTORNO);
                    endTime = System.nanoTime();
                }
            }
            mostrarResultados(solucion, nombreArchivo, nombreAlgoritmo, (endTime - startTime));
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void mostrarResultados(ArrayList<Integer> solucion, String nombreArchivo, String nombreAlgoritmo, long duracion){
        double costeTotal = 0;
        for(int j = 0; j < (solucion.size() - 1); ++j){
            if(j == (solucion.size()) - 2) costeTotal += AlmacenarDatos.getDistancias()[solucion.get(j)][solucion.get(0)];
            else costeTotal += AlmacenarDatos.getDistancias()[solucion.get(j)][solucion.get(j + 1)];
        }
        System.out.println("Problema " + nombreArchivo);
        System.out.println("Algoritmo: " + nombreAlgoritmo);
        System.out.println("Coste de " + costeTotal);
        System.out.println("Tiempo de ejecución: " + (duracion/1_000_000.0) + " milisegundos");
        System.out.println("Solucion: " + solucion);
    }
}