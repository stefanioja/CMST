import com.gurobi.gurobi.GRBException;
import org.example.CMSTMILPModel;
import org.example.EsauWilliamsOptimized;
import org.example.InstanceConverter;
import org.graph4j.Graph;
import org.graph4j.generators.EdgeWeightsGenerator;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.util.EdgeSet;
import org.jgrapht.alg.spanning.EsauWilliamsCapacitatedMinimumSpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EsauWilliamsLPComparison {
    public static void main(String[] args) throws GRBException {
        int[] sizes      = {10, 20, 50, 100, 200, 230};
        int   iterations = 5;
        int   root       = 0;

        for (int n : sizes) {

            double capacity = (int)Math.ceil(0.2 * n);          // FIX-1: păstrăm tipul double
            double totalWeightG4J = 0.0;
            double totalWeightLP = 0.0;

            for (int it = 0; it < iterations; it++) {

                // ──────────── 1. instanță Graph4J ────────────
                Graph g4j = GraphGenerator.complete(n);
                EdgeWeightsGenerator.randomIntegers(g4j, 1, 100);

                int[] demands = new int[n];
                Arrays.fill(demands, 1);

                EsauWilliamsOptimized ew4j =
                        new EsauWilliamsOptimized(g4j, root ,(int)capacity, demands);
                ew4j.getTree();
                totalWeightG4J += ew4j.getWeight();

                // ──────────── 2. convertim către JGraphT ────────────
                CMSTMILPModel cmst = new CMSTMILPModel(g4j, (int)capacity);
                cmst.optimize();
                EdgeSet edgeSet = cmst.getTreeEdges();
                totalWeightLP += edgeSet.weight();
            }

            double avgG4J = totalWeightG4J / iterations;
            double avgLP = totalWeightLP  / iterations;
            double diff   = avgG4J - avgLP;

            System.out.printf(                         // FIX-4: folosim «capacity» la print
                    "n=%d, cap=%.0f: Graph4J avg=%.2f, LP avg=%.2f, diff=%.2f%n",
                    n, capacity, avgG4J, avgLP, diff);
        }
    }
}
