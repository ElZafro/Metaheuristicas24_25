import java.util.ArrayList;
import java.util.Random;

public class LocalSearch {
    private static double evaluacionCoste(ArrayList<Integer> solucionInicial){
        double coste = 0;
        for(int i = 0; i < (solucionInicial.size() - 1); ++i){
            if(i == (solucionInicial.size() - 2)) coste += AlmacenarDatos.getDistancias()[solucionInicial.get(i)][solucionInicial.get(0)];
            else coste += AlmacenarDatos.getDistancias()[solucionInicial.get(i)][solucionInicial.get(i + 1)];
        }
        return coste;
    }

    private static double evaluacionFactorizacion(int posCiudad1, int posCiudad2, ArrayList<Integer> sol, int tamSolucion, double mejorCoste/*ArrayList<Integer> solucionActual, ArrayList<Integer> solucionVecina, double costeActual*/){
        if(posCiudad1 > posCiudad2){
            int temp = posCiudad1;
            posCiudad1 = posCiudad2;
            posCiudad2 = temp;
        }
        double[][] matriz = AlmacenarDatos.getDistancias();
        double coste = mejorCoste;

        if(Math.abs(posCiudad1 - posCiudad2) == 1) coste += (2 * matriz[sol.get(posCiudad1)][sol.get(posCiudad2)]);
        if(posCiudad1 == 0){
            if(posCiudad2 == (tamSolucion - 1)){
                coste -= (matriz[sol.get(posCiudad1)][sol.get(posCiudad1 + 1)] + matriz[sol.get(posCiudad2 - 1)][sol.get(posCiudad2)]);
                coste += (matriz[sol.get(posCiudad2)][sol.get(posCiudad1 + 1)] + matriz[sol.get(posCiudad2 - 1)][sol.get(posCiudad1)]);
            } else{
                coste -= (matriz[sol.get(posCiudad1)][sol.get(posCiudad1 + 1)] + matriz[sol.get(tamSolucion - 1)][sol.get(posCiudad1)] + matriz[sol.get(posCiudad2)][sol.get(posCiudad2 + 1)] + matriz[sol.get(posCiudad2 - 1)][sol.get(posCiudad2)]);
                coste += (matriz[sol.get(posCiudad2)][sol.get(posCiudad1 + 1)] + matriz[sol.get(posCiudad2)][sol.get(tamSolucion - 1)] + matriz[sol.get(posCiudad1)][sol.get(posCiudad2 - 1)] + matriz[sol.get(posCiudad1)][posCiudad2 + 1]);
            }
        } else{
            if(posCiudad2 == (tamSolucion - 1)){
                coste -= (matriz[sol.get(posCiudad1 - 1)][sol.get(posCiudad1)] + matriz[sol.get(posCiudad1)][posCiudad1 + 1] + matriz[sol.get(posCiudad2 - 1)][sol.get(posCiudad2)] + matriz[sol.get(posCiudad2)][sol.get(0)]);
                coste += (matriz[sol.get(posCiudad1)][sol.get(posCiudad2 - 1)] + matriz[sol.get(0)][sol.get(posCiudad1)] + matriz[sol.get(posCiudad2)][sol.get(posCiudad1 - 1)] + matriz[sol.get(posCiudad2)][sol.get(posCiudad1 + 1)]);
            } else{
                coste -= (matriz[sol.get(posCiudad1 - 1)][sol.get(posCiudad1)] + matriz[sol.get(posCiudad1)][sol.get(posCiudad1 + 1)] + matriz[sol.get(posCiudad2 - 1)][sol.get(posCiudad2)] + matriz[sol.get(posCiudad2)][sol.get(posCiudad2 + 1)]);
                coste += (matriz[sol.get(posCiudad1)][sol.get(posCiudad2 - 1)] + matriz[sol.get(posCiudad1)][sol.get(posCiudad2 + 1)] + matriz[sol.get(posCiudad2)][sol.get(posCiudad1 - 1)] + matriz[sol.get(posCiudad2)][sol.get(posCiudad1 + 1)]);
            }
        }


        return coste;
    }

/*
    private static ArrayList<ArrayList<Integer>> generarVecindario(ArrayList<Integer> solucionActual, float tamVecindario){
        ///TODO FUNCION BUENA HASTA AHORA

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

    }*/

    private static ArrayList<Integer> swapPosiciones(ArrayList<Integer> solucionActual, int posCiudad1, int posCiudad2){
        ArrayList<Integer> nuevaSolucion = new ArrayList<>();
        for(Integer integer: solucionActual){
            nuevaSolucion.add(integer);
        }
        int temp = nuevaSolucion.get(posCiudad1);
        nuevaSolucion.set(posCiudad1, nuevaSolucion.get(posCiudad2));
        nuevaSolucion.set(posCiudad2, temp);
        return nuevaSolucion;
    }

    public static ArrayList<Integer> busquedalocal(ArrayList<Integer> solucionInicial, int totalIteraciones, int porcentajeEntornoInicial, int porcentajeCambio, int porcentajeReduccion, long semilla){
        double mejorCoste = evaluacionCoste(solucionInicial);
        boolean mejora = true;
        int iteracion = 0;
        float contReduccionEntorno = (totalIteraciones * ((float) porcentajeCambio/100));
        float tamEntorno = (((float) porcentajeEntornoInicial /100) * totalIteraciones);
        while(mejora && (iteracion <= totalIteraciones)){
            mejora = false;
            if(contReduccionEntorno == 0){
                contReduccionEntorno = totalIteraciones * ((float) porcentajeCambio/100);
                tamEntorno = tamEntorno - (tamEntorno * ((float) porcentajeReduccion/100));
                System.out.println("Se ha reducido tamEntorno: " + tamEntorno);
            }
            ArrayList<Integer> solucionActual = new ArrayList<>(solucionInicial);
            ArrayList<Integer> solucionMejorVecino = new ArrayList<>();
            double mejorCosteVecino = mejorCoste;
            Random random = new Random(semilla);
            for(int i = 0; i < tamEntorno; ++i){
                int ciudad1 = random.nextInt(solucionActual.size()), ciudad2 = random.nextInt(solucionActual.size());
                if(ciudad1 == ciudad2){
                    boolean cambio = false;
                    while(!cambio){
                        ciudad2 = random.nextInt(solucionActual.size());
                        if(ciudad1 != ciudad2) cambio = true;
                    }
                }

                double costeVecino = evaluacionFactorizacion(ciudad1, ciudad2, solucionActual, solucionActual.size(), mejorCoste);
                if(costeVecino < mejorCosteVecino){
                    solucionMejorVecino = swapPosiciones(solucionActual, ciudad1, ciudad2);
                    mejorCosteVecino = costeVecino;
                    mejora = true;
                }
            }
            if(mejora){
                iteracion++;
                System.out.println("Iteracion " + iteracion);
                contReduccionEntorno--;
                mejorCoste = mejorCosteVecino;
                solucionInicial = solucionMejorVecino;
            }
        }
        return solucionInicial;
        ///TODO SOLUCIÓN BUENA HASTA AHORA
        /*
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
         */
    }
}