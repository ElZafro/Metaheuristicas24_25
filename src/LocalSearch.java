import java.util.ArrayList;
import java.util.Arrays;

public class LocalSearch {
    private static double evaluacionCoste(ArrayList<Integer> solucionInicial){
        double coste = 0;
        for(int i = 0; i < (solucionInicial.size() - 1); ++i){
            if(i == (solucionInicial.size() - 2)) coste += AlmacenarDatos.getDistancias()[solucionInicial.get(i)][solucionInicial.get(0)];
            else coste += AlmacenarDatos.getDistancias()[solucionInicial.get(i)][solucionInicial.get(i + 1)];
        }
        return coste;
    }

    ///TODO REVISAR Y TERMINAR MÉTODO
    private static double evaluacionFactorizacion(ArrayList<Integer> solucionActual, ArrayList<Integer> solucionVecina, double costeActual){
        /*if (j == (i + 1) % l) {
            return solucionActual
                    - this.distances[s[(i + l - 1) % l]][s[i]]
                    - this.distances[s[j]][s[(j + 1) % l]]
                    + this.distances[s[(i + l - 1) % l]][s[j]]
                    + this.distances[s[i]][s[(j + 1) % l]];
        }

        if (i == (j + 1) % l) {
            return prevSolution.cost
                    - this.distances[s[(j + l - 1) % l]][s[j]]
                    - this.distances[s[i]][s[(i + 1) % l]]
                    + this.distances[s[(j + l - 1) % l]][s[i]]
                    + this.distances[s[j]][s[(i + 1) % l]];
        }

        return prevSolution.cost
                - this.distances[s[(i + l - 1) % l]][s[i]]
                - this.distances[s[i]][s[(i + 1) % l]]
                - this.distances[s[(j + l - 1) % l]][s[j]]
                - this.distances[s[j]][s[(j + 1) % l]]

                + this.distances[s[(i + l - 1) % l]][s[j]]
                + this.distances[s[j]][s[(i + 1) % l]]
                + this.distances[s[(j + l - 1) % l]][s[i]]
                + this.distances[s[i]][s[(j + 1) % l]];
        */
        double nuevoCoste = costeActual;
        for(int i = 0; i < (solucionActual.size() - 1); ++i){
            int ciudadActual = solucionActual.get(i);
            int siguienteCiudad = solucionActual.get(i + 1);
            nuevoCoste -= AlmacenarDatos.getDistancias()[ciudadActual][siguienteCiudad];
        }
        nuevoCoste -= AlmacenarDatos.getDistancias()[solucionActual.get(solucionActual.size() - 1)][solucionActual.get(0)];
        for(int i = 0; i < (solucionVecina.size() - 1); ++i) {
            int ciudadActual = solucionVecina.get(i);
            int siguienteCiudad = solucionVecina.get(i + 1);
            nuevoCoste += AlmacenarDatos.getDistancias()[ciudadActual][siguienteCiudad];  // Sumar el coste del nuevo arco
        }
        nuevoCoste += AlmacenarDatos.getDistancias()[solucionVecina.get(solucionVecina.size() - 1)][solucionVecina.get(0)];

        return nuevoCoste;
    }

    private static ArrayList<ArrayList<Integer>> generarVecindario(ArrayList<Integer> solucionActual, float tamVecindario){
        ArrayList<ArrayList<Integer>> vecindario = new ArrayList<>((int)tamVecindario);
        int cont = 0;
        for(int i = 0; i < solucionActual.size(); ++i){
            for (int j = i + 1; j < solucionActual.size(); ++j) {
                int[] nuevaSolucion = new int[solucionActual.size()];
                for(int k = 0; k < solucionActual.size(); ++k){
                    nuevaSolucion[k] = solucionActual.get(k);
                }
                swapPosiciones(nuevaSolucion, i, j);
                ArrayList<Integer> nuevoVecino = new ArrayList<>();
                for(int k = 0; k < solucionActual.size(); ++k){
                    nuevoVecino.add(nuevaSolucion[k]);
                }
                vecindario.add(nuevoVecino);
                cont++;
                if(cont == tamVecindario) break;
            }
            if(cont == tamVecindario) break;
        }
        return vecindario;
    }

    private static void swapPosiciones(int[] vector, int inicio, int fin){
        while(inicio < fin){
            int temp = vector[inicio];
            vector[inicio] = vector[fin];
            vector[fin] = temp;
            inicio++;
            fin--;
        }
    }

    public static ArrayList<Integer> busquedalocal(ArrayList<Integer> solucionInicial, int totalIteraciones, int porcentajeEntornoInicial, int porcentajeCambio, int porcentajeReduccion){
        double mejorCoste = evaluacionCoste(solucionInicial);
        boolean mejora = true;
        int iteracion = 0;
        float contReduccionEntorno = (totalIteraciones * ((float) porcentajeCambio/100));
        float tamEntorno = (((float) porcentajeEntornoInicial /100) * totalIteraciones);
        while(mejora && (iteracion <= totalIteraciones)){
            mejora = false;
            if(contReduccionEntorno == 0){
                contReduccionEntorno = totalIteraciones * ((float) porcentajeCambio/100);
                tamEntorno -= tamEntorno * ((float) porcentajeReduccion/100);
            }
            ArrayList<Integer> solucionActual = new ArrayList<>(solucionInicial);
            ArrayList<Integer> solucionMejorVecino = new ArrayList<>();
            double mejorCosteVecino = mejorCoste;
            for(ArrayList<Integer> vecino: generarVecindario(solucionActual, tamEntorno)){
                double costeVecino = evaluacionFactorizacion(solucionActual, vecino, mejorCoste);
                if(costeVecino < mejorCoste){
                    solucionMejorVecino = vecino;
                    mejorCosteVecino = costeVecino;
                    mejora = true;
                }
            }
            if(mejora){
                iteracion++;
                contReduccionEntorno--;
                mejorCoste = mejorCosteVecino;
                solucionInicial = solucionMejorVecino;
            }
        }
        return solucionInicial;
    }

}