import java.util.*;

public class GreedyAleatorio {

    // Clase auxiliar para almacenar pares (suma, índice)
    static class Pair implements Comparable<Pair> {
        double sum;
        int index;

        public Pair(double sum, int index) {
            this.sum = sum;
            this.index = index;
        }

        @Override
        public int compareTo(Pair other) {
            return Double.compare(this.sum, other.sum);
        }
    }

    public static void greedy(List<Integer> s, int n, double[][] mat, int limitBusqueda, Random random) {
        // TreeSet que mantendrá los pares (suma, índice) ordenados por suma
        TreeSet<Pair> sumatorios = new TreeSet<>();
        List<Integer> vectorSolucion = new ArrayList<>(Collections.nCopies(n, 0));

        // Calcular las sumatorias de distancias para cada ciudad
        for(int i = 0; i < n; i++){
            double suma = 0;
            for(int j = 0; j < n; j++){
                if(i != j) suma += mat[i][j];
            }
            // Insertar cada par (suma, índice) en el conjunto ordenado
            sumatorios.add(new Pair(suma, i));
        }

        // Construir la solución
        for(int i = 0; i < n; i++) {
            // Ajustar limitBusqueda si queda menos del tamaño de la lista restante
            if((n - i - 1) < limitBusqueda) limitBusqueda = n - i - 2;

            // Generar un número aleatorio entre 1 y limitBusqueda
            int posicion = random.nextInt(limitBusqueda) + 1;

            // Iterar sobre el TreeSet hasta alcanzar el índice aleatorio `posicion`
            Iterator<Pair> it = sumatorios.iterator();
            int p = 1;
            while(p < posicion && it.hasNext()){
                it.next();
                p++;
            }

            // Asignar la ciudad seleccionada a la solución
            Pair selectedPair = it.next();
            vectorSolucion.set(i, selectedPair.index);

            // Eliminar el elemento seleccionado del TreeSet
            sumatorios.remove(selectedPair);
        }

        // Asignar la solución calculada a la lista pasada por referencia
        s.clear();
        s.addAll(vectorSolucion);
    }
}