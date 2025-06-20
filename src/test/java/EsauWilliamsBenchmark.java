import org.example.EsauWilliamsOptimized;
import org.example.InstanceConverter;
import org.graph4j.Graph;
import org.graph4j.generators.EdgeWeightsGenerator;
import org.graph4j.generators.GraphGenerator;
import org.jgrapht.alg.spanning.EsauWilliamsCapacitatedMinimumSpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;

public class EsauWilliamsBenchmark {

    public static void main(String[] args) {

        int[] sizes      = {10, 20, 50, 100, 200, 300, 400, 500};
        int   iterations = 10;
        int   root       = 0;

        for (int n : sizes) {

            double capacity = Math.ceil(0.1 * n);

            long totalNsG4J = 0;
            long totalNsJGT = 0;

            for (int it = 0; it < iterations; it++) {

//                Graph g4j = GraphGenerator.randomGnp(n, 0.1);
//                for(int i: g4j.vertices()){
//                    if(g4j.containsEdge(i, 0)) continue;
//                    g4j.addEdge(i, 0);
//                }

                Graph g4j = GraphGenerator.complete(n);
                EdgeWeightsGenerator.randomIntegers(g4j, 1, 100);

                int[] demands = new int[n];
                Arrays.fill(demands, 1);

                long t0 = System.nanoTime();
                EsauWilliamsOptimized ew4j =
                        new EsauWilliamsOptimized(g4j, root, (int) capacity, demands);
                ew4j.getTree();
                long t1 = System.nanoTime();
                totalNsG4J += (t1 - t0);

                InstanceConverter ic =
                        new InstanceConverter(g4j);
                SimpleWeightedGraph<Integer, DefaultWeightedEdge> jgt = ic.getJGraphT();

                Map<Integer, Double> demandMap = new HashMap<>();
                for (int v : jgt.vertexSet()) demandMap.put(v, 1.0);

                long t2 = System.nanoTime();
                EsauWilliamsCapacitatedMinimumSpanningTree<Integer, DefaultWeightedEdge> jgtAlg =
                        new EsauWilliamsCapacitatedMinimumSpanningTree<>(
                                jgt, root, capacity, demandMap, 1);
                jgtAlg.getCapacitatedSpanningTree();
                long t3 = System.nanoTime();
                totalNsJGT += (t3 - t2);
            }

            double avgMsG4J = totalNsG4J / 1_000_000.0 / iterations;
            double avgMsJGT = totalNsJGT / 1_000_000.0 / iterations;
            double speedUp  = avgMsJGT / avgMsG4J;

            System.out.printf(
                    Locale.US,
                    "n=%d, K=%.0f: Graph4J=%.3f ms | JGraphT=%.3f ms | speed-up=%.2fx%n",
                    n, capacity, avgMsG4J, avgMsJGT, speedUp);
        }
    }
}
