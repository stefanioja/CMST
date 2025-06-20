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

import java.util.*;

public class LPBenchmark {

    public static void main(String[] args) throws GRBException {
        int[] sizes      = {10, 20, 30, 40, 50, 60, 70,80, 90, 100, 150, 200, 400, 500, 600, 700, 800, 900};
        int root = 0;

        for(int n: sizes) {
            Graph g = GraphGenerator.complete(n);
            EdgeWeightsGenerator.randomIntegers(g, 1, 100);

            int capacity = (int) Math.ceil(0.2 * n);
            CMSTMILPModel cmst = new CMSTMILPModel(g, capacity);
            cmst.optimize();
            EdgeSet edgeSet = cmst.getTreeEdges();
            System.out.println(edgeSet.weight());
        }
    }
}
