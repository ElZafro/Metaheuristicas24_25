import java.util.PriorityQueue;

public class Dijkstra {

    public static void Dijkstra(double[] initialDistances, double[][] adjacencyMatrix, int source) {
        int n = adjacencyMatrix.length;
        PriorityQueue<Pair> pq = new PriorityQueue<>((a, b) -> Double.compare(a.weight, b.weight));
        pq.offer(new Pair(source, initialDistances[source]));

        for (int i = 0; i < n; i++) {
            if (i != source) {
                pq.offer(new Pair(i, initialDistances[i]));
            }
        }

        while (!pq.isEmpty()) {
            Pair p = pq.poll();
            int u = p.node;
            if (p.weight > initialDistances[u]) continue; // Si la distancia actual es mayor, se descarta
            for (int v = 0; v < n; v++) {
                if (adjacencyMatrix[u][v] != 0) { // Si hay una arista entre u y v
                    if (p.weight + adjacencyMatrix[u][v] < initialDistances[v]) {
                        initialDistances[v] = p.weight + adjacencyMatrix[u][v];
                        pq.offer(new Pair(v, initialDistances[v]));
                    }
                }
            }
        }
    }

    static class Pair {
        int node;
        double weight;
        public Pair(int node, double weight) {
            this.node = node;
            this.weight = weight;
        }
    }
}