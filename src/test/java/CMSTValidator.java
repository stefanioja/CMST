import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.GraphTests;
import org.graph4j.connectivity.ConnectivityAlgorithm;

public class CMSTValidator {
    public static boolean validate(Graph original, Graph graph, int root, int capacity, int[] demands) {
        if(graph.numVertices() != original.numVertices()) return false;

        if(!GraphTests.isTree(graph)){
            System.out.println("Graph is not a tree");
            return false;
        }

        graph.removeVertex(root);
        ConnectivityAlgorithm ca = new ConnectivityAlgorithm(graph);
        for (Graph g: ca.getConnectedComponents()) {
            int componentSize = 0;
            for (int i : g.vertices()) {
                componentSize += demands[original.indexOf(i)];
            }
            if (componentSize > capacity) {
                System.out.println("invalid capacity");
                return false;
            }
        }

        return true;
    }
}
