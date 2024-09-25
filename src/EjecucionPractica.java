import java.util.ArrayList;

public class EjecucionPractica {
    private static int LIMIT = 5;

    private static long SEMILLA = 77688090;

    private final String[] nombresArchivos = {"a280.tsp", "ch130.tsp", "d18512.tsp", "pr144.tsp", "u1060.tsp"};

    public EjecucionPractica() {}

    public void exec(){
        for(int i = 0; i < nombresArchivos.length; ++i){
            AlmacenarDatos.inicializacionDatos(nombresArchivos[i]);
            ArrayList<Integer> solucion = GreedyAleatorio.greedy(AlmacenarDatos.getDistancias().length, AlmacenarDatos.getDistancias(), LIMIT, SEMILLA);
            double costeTotal = 0;
            for(int j = 0; j < (solucion.size() - 1); ++j){
                costeTotal += AlmacenarDatos.getDistancias()[solucion.get(j)][solucion.get(j + 1)];
            }
            System.out.println("Problema " + nombresArchivos[i] + ": coste de " + costeTotal);
            System.out.println("Solucion: " + solucion);
        }
    }
}