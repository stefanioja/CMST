import org.example.CapacitatedMinimumSpanningTreeBase;
import org.example.EsauWilliamsOptimized;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.generators.EdgeWeightsGenerator;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.spanning.KruskalMinimumSpanningTree;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TestEsauCapacitatedTree {
    @Test
    public void testDisconnected(){
        Graph g = GraphGenerator.randomGnm(50, 23);
        EdgeWeightsGenerator.randomIntegers(g, 1, 100);

        int[] demands = new int[50];
        Arrays.fill(demands, 1);

        CapacitatedMinimumSpanningTreeBase ew = new EsauWilliamsOptimized(g, 0, 3, demands);
        assertThrows(IllegalArgumentException.class, ew::getEdges);
    }

    @Test
    public void testComplete(){
        Graph g = GraphGenerator.complete(50);
        EdgeWeightsGenerator.randomIntegers(g, 1, 100);

        int[] demands = new int[50];
        Arrays.fill(demands, 1);

        CapacitatedMinimumSpanningTreeBase ew = new EsauWilliamsOptimized(g, 0, 3, demands);
        assertTrue(CMSTValidator.validate(g, ew.getTree(), 0, 3, demands));
    }
    @Test
    public void testRandomGraph(){
        int[] n = {10, 20, 50, 100, 200, 500};
        for(int i: n){
            Graph g = GraphGenerator.randomGnp(i, 0.1);
            EdgeWeightsGenerator.randomIntegers(g, 1, 100);

            int[] demands = new int[i];
            Arrays.fill(demands, 1);

            CapacitatedMinimumSpanningTreeBase ew = new EsauWilliamsOptimized(g, 0, 3, demands);
            try{
                ew.getEdges();
                assertTrue(CMSTValidator.validate(g, ew.getTree(), 0, 3, demands));
            } catch(IllegalArgumentException e){
                assertFalse(CMSTValidator.validate(g, ew.getTree(), 0, 3, demands));
            }
        }

    }

    @Test
    public void testTrivial(){
        Graph g = GraphBuilder.numVertices(1).buildGraph();

        int[] demands = new int[1];
        Arrays.fill(demands, 1);

        CapacitatedMinimumSpanningTreeBase ew = new EsauWilliamsOptimized(g, 0, 3, demands);

        KruskalMinimumSpanningTree kmst = new KruskalMinimumSpanningTree(g);
        System.out.println(kmst.getTree());
        assertTrue(CMSTValidator.validate(g, ew.getTree(), 0, 3, demands));
    }
}
