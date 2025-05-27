package org.example;

import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.connectivity.ConnectivityAlgorithm;
import org.graph4j.route.CycleFinder;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.UnionFind;

/*
cea mai buna versiune implementata, asta am folosit in benchmarkuri
 */
public class EsauWilliams extends CapacitatedMinimumSpanningTreeBase {

    public EsauWilliams(Graph g, int root, int capacity, int[] costs) {
        super(g, root, capacity, costs);
    }

    @Override
    protected void compute() {
        int n = graph.numVertices();
        UnionFind uf = new UnionFind(n);
        this.treeEdges = new EdgeSet(graph, n - 1);
        this.minWeight = 0.0;

        int[] closest = new int[n];
        int[] subtreeCosts = new int[n];
        double[] savings = new double[n];
        double[] gates = new double[n];

        boolean[] visited = new boolean[n];

        for (int i : graph.vertices()) {
            if (i == root) continue;
            int id = graph.indexOf(i);

            //init gates -> shortest path length from component to root
            gates[id] = graph.edge(i, root).weight();

            //capacity used by the component
            subtreeCosts[id] = costs[id];

            //closest vertex
            closest[id] = findClosest(i, uf, subtreeCosts);
            int j = closest[id];
            if (j == -1) {
                savings[id] = -1;
            } else {
                int idJ = graph.indexOf(j);
                //ew heuristic
                savings[id] = Math.max(gates[id], gates[idJ]) - graph.edge(i, j).weight();
            }

            visited[id] = false;
        }

        while (true) {
            Edge bestEdge = null;
            double sMax = Double.MIN_VALUE;

            for (int i : graph.vertices()) {
                if (i == root) continue;
                int id = graph.indexOf(i);

                double saving = savings[id];
                if (saving <= 0) continue; //we ignore non profitable moves

                if (saving > sMax) {
                    sMax = saving;
                    bestEdge = graph.edge(i, closest[id]);
                }
            }

            if (bestEdge == null) break;

            treeEdges.add(bestEdge);
            this.minWeight += bestEdge.weight();

            //reconfiguration
            int idI = graph.indexOf(bestEdge.source());
            int idJ = graph.indexOf(bestEdge.target());

            int rootI = uf.find(idI);
            int rootJ = uf.find(idJ);

            uf.union(rootI, rootJ);
            int newRoot = uf.find(idI);

            int newSize = subtreeCosts[rootI] + subtreeCosts[rootJ];
            double newGate = Math.min(gates[rootI], gates[rootJ]); //gates should be the shortest
            subtreeCosts[newRoot] = newSize;
            gates[newRoot] = newGate;

            for (int i : graph.vertices()) {
                if (i == root) continue;
                int id = graph.indexOf(i);

                closest[id] = findClosest(i, uf, subtreeCosts);
                int j = closest[id];
                if (j == -1) {
                    savings[id] = -1;
                } else {
                    int idJ2 = graph.indexOf(j);
                    int rootI2 = uf.find(id);
                    int rootJ2 = uf.find(idJ2);

                    Edge e = graph.edge(i, j);
                    if (e != null) {
                        savings[id] = Math.max(gates[rootI2], gates[rootJ2]) - e.weight();
                    } else {
                        savings[id] = -1;
                    }
                }
            }
        }

        for(int i : graph.vertices()) {
            if (i == root) continue;
            int id = graph.indexOf(i);
            int rootI = uf.find(id);

            if(graph.edge(i, root).weight() == gates[rootI] && !visited[rootI]) {
                visited[rootI] = true;
                Edge toRoot = graph.edge(i, root);
                if (toRoot != null) {
                    treeEdges.add(toRoot);
                    this.minWeight += toRoot.weight();
                } else {
                    //exception aici
                }
            }
        }

        this.validateCMST(uf);
    }

    private int findClosest(int v, UnionFind uf, int[] subtreeCosts) {
        int uID = graph.indexOf(v);
        int best = -1;
        double minWeight = Double.MAX_VALUE;

        for (Edge e : graph.edgesOf(v)) {
            int vID = graph.indexOf(e.target());

            int rootU = uf.find(uID);
            int rootV = uf.find(vID);

            if (rootU == rootV) continue;
            if (subtreeCosts[rootU] + subtreeCosts[rootV] > this.capacity) continue;


            if (e.weight() < minWeight) {
                best = e.target();
                minWeight = e.weight();
            }
        }

        return best;
    }

    public boolean validateCMST(UnionFind uf){
        if(treeEdges.size() != graph.numVertices() - 1){
            System.out.println("Not a valid tree");
            return false;
        }

        int[] componentsSize = new int[graph.numVertices()];

        for (int i : graph.vertices()) {
            componentsSize[i] = 0;
        }

        for (int i : graph.vertices()) {
            int id = graph.indexOf(i);
            int rootI = uf.find(id);

            componentsSize[rootI]++;
            if(componentsSize[rootI] > this.capacity){
                System.out.println("Not a valid capacity");
                return false;
            }
        }

        Graph g = GraphBuilder.numVertices(this.graph.numVertices()).buildGraph();
        for (Edge e : this.treeEdges) {
            g.addEdge(e.source(), e.target(), e.weight());
        }

        CycleFinder cf = new CycleFinder(g);
        if(cf.containsCycle()){
            System.out.println("Cycle found");
            return false;
        }

        ConnectivityAlgorithm ca = new ConnectivityAlgorithm(g);
        if(ca.countConnectedComponents() != 1){
            System.out.println("Not connected components");
            return false;
        }

        return true;
    }
}