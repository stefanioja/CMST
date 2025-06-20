import com.gurobi.gurobi.GRBException;
import org.example.CMSTMILPModel;
import org.example.CapacitatedMinimumSpanningTreeBase;
import org.example.EsauWilliamsOptimized;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.generators.EdgeWeightsGenerator;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.util.EdgeSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TestLPCapacitatedTree {
    @Test
    public void testComplete(){
        Graph g = GraphGenerator.complete(50);
        EdgeWeightsGenerator.randomIntegers(g, 1, 100);

        int[] demands = new int[50];
        Arrays.fill(demands, 1);

        CMSTMILPModel cmst = new CMSTMILPModel(g, 3);
        cmst.optimize();
        EdgeSet edgeSet = cmst.getTreeEdges();

        Graph nG = GraphBuilder.numVertices(50).buildGraph();
        for(Edge e : edgeSet){
            nG.addEdge(e);
        }

        assertTrue(CMSTValidator.validate(g, nG, 0, 3, demands));
    }
}