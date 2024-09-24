public class EjecucionPractica {

    private final String[] nombresArchivos = {"a280.tsp", "ch130.tsp", "d18512.tsp", "pr144.tsp", "u1060.tsp"};

    public EjecucionPractica() {}

    public void exec(){
        for(int i = 0; i < nombresArchivos.length; ++i){
            AlmacenarDatos.inicializacionDatos(nombresArchivos[i]);

        }
    }
}