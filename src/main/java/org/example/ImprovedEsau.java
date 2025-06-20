//package org.example;
//
//import org.graph4j.Edge;
//import org.graph4j.Graph;
//import org.graph4j.GraphBuilder;
//import org.graph4j.connectivity.ConnectivityAlgorithm;
//import org.graph4j.route.CycleFinder;
//import org.graph4j.util.EdgeSet;
//import org.graph4j.util.UnionFind;
//
//import java.util.Comparator;
//import java.util.PriorityQueue;
//
//public class ImprovedEsau extends CapacitatedMinimumSpanningTreeBase {
//
//    PriorityQueue<Edge>[] neighborPQ;
//    public ImprovedEsau(Graph g, int root, int capacity, int[] costs) {
//        super(g, root, capacity, costs);
//    }
//
//    protected void compute() {
//        int n = graph.numVertices();
//        UnionFind uf = new UnionFind(n);
//        this.treeEdges = new EdgeSet(graph, n - 1);
//        this.minWeight = 0.0;
//
//        int[] closest = new int[n];
//        int[] subtreeCosts = new int[n];
//        double[] savings = new double[n];
//        double[] gates = new double[n];
//
//        boolean[] visited = new boolean[n];
//
//
//        neighborPQ = new PriorityQueue[n];
//        for (int i : graph.vertices()) {
//            if (i == root) continue;
//            int id = graph.indexOf(i);
//
//            //init gates -> length of the shortest path from component to root
//            if(graph.containsEdge(id, root)) gates[id] = graph.edge(i, root).weight(); else gates[id] = Double.MAX_VALUE;
//
//            //capacity used by the component
//            subtreeCosts[id] = costs[id];
//
//            neighborPQ[id] = new PriorityQueue<>(Comparator.comparingDouble(Edge::weight));
//            for (Edge e : graph.edgesOf(i)) {
//                neighborPQ[id].offer(e);
//            }
//
//            //closest vertex
//            closest[id] = findClosest(i, uf, subtreeCosts);
//            int j = closest[id];
//            if (j == -1) {
//                savings[id] = -1;
//            } else {
//                int idJ = graph.indexOf(j);
//                //ew heuristic
//                savings[id] = Math.max(gates[id], gates[idJ]) - graph.edge(i, j).weight();
//            }
//
//            visited[id] = false;
//        }
//
//        while (true) {
//            Edge bestEdge = null;
//            double sMax = Double.MIN_VALUE;
//
//            for (int i : graph.vertices()) {
//                if (i == root) continue;
//                int id = graph.indexOf(i);
//
//                double saving = savings[id];
//                if (saving <= 0) continue; //we ignore non profitable moves
//
//                if (saving > sMax) {
//                    sMax = saving;
//                    bestEdge = graph.edge(i, closest[id]);
//                }
//            }
//
//            if (bestEdge == null) break;
//
//            treeEdges.add(bestEdge);
//            this.minWeight += bestEdge.weight();
//
//            //reconfiguration
//            int idI = graph.indexOf(bestEdge.source());
//            int idJ = graph.indexOf(bestEdge.target());
//
//            int rootI = uf.find(idI);
//            int rootJ = uf.find(idJ);
//
//            uf.union(rootI, rootJ);
//            int newRoot = uf.find(idI);
//
//            int newSize = subtreeCosts[rootI] + subtreeCosts[rootJ];
//            double newGate = Math.min(gates[rootI], gates[rootJ]); //gates should be the shortest
//            subtreeCosts[newRoot] = newSize;
//            gates[newRoot] = newGate;
//
//            for (int i : graph.vertices()) {
//                if (i == root) continue;
//                int id = graph.indexOf(i);
//
//                if(savings[id] < 0) continue;
//                int j = closest[id];
//                int jId = graph.indexOf(j);
//
//                if(uf.find(id) == uf.find(jId) || subtreeCosts[uf.find(id)] + subtreeCosts[uf.find(jId)] > this.capacity) {
//                    closest[id] = findClosest(i, uf, subtreeCosts);
//                    j = closest[id];
//                }
//
//                if (j == -1) {
//                    savings[id] = -1;
//                } else{
//                    int idJ2 = graph.indexOf(j);
//                    int rootI2 = uf.find(id);
//                    int rootJ2 = uf.find(idJ2);
//
//                    Edge e = graph.edge(i, j);
//                    if (e != null) {
//                        savings[id] = Math.max(gates[rootI2], gates[rootJ2]) - e.weight();
//                    } else {
//                        savings[id] = -1;
//                    }
//                }
//            }
//        }
//
//        //connecting to root
//        for(int i : graph.vertices()) {
//            if (i == root) continue;
//
//            if(graph.containsEdge(i, root)) {
//                int id = graph.indexOf(i);
//                int rootI = uf.find(id);
//                if(graph.edge(i, root).weight() == gates[rootI] && !visited[rootI]) {
//                    visited[rootI] = true;
//                    Edge toRoot = graph.edge(i, root);
//                    treeEdges.add(toRoot);
//                    this.minWeight += toRoot.weight();
//                }
//            }
//        }
//
//        //final validation
//        if(!this.validateCMST(uf))
//            throw new IllegalArgumentException("This graph does not have a capacitated minimum spanning tree with the given capacity and demands.");
//    }
//
//    //change to iterator
//    private int findClosest(int v, UnionFind uf, int[] subtreeCosts) {
//        int uID = graph.indexOf(v);
//        int best = -1;
//
//        while(!neighborPQ[uID].isEmpty()) {
//            Edge e = neighborPQ[uID].poll();
//            if(e.target() == root) continue;
//            int src = uf.find(uID);
//            int dest = uf.find(graph.indexOf(e.target()));
//            if(src == dest) continue;
//            if(subtreeCosts[src] + subtreeCosts[dest] > this.capacity) continue;
//
//            best = dest;
//            break;
//        }
//
//        return best;
//    }
//
//    public boolean validateCMST(UnionFind uf){
//        //check valid tree -> n-1 edges
//        if(treeEdges.size() != graph.numVertices() - 1) return false;
//
//
//
//        int[] componentsSize = new int[graph.numVertices()];
//
//        for (int i : graph.vertices()) {
//            int id = graph.indexOf(i);
//            int rootI = uf.find(id);
//
//            componentsSize[rootI]++;
//            //check capacity
//            if(componentsSize[rootI] > this.capacity) return false;
//        }
//
//        Graph g = GraphBuilder.numVertices(this.graph.numVertices()).buildGraph();
//        for (Edge e : this.treeEdges) {
//            g.addEdge(e.source(), e.target(), e.weight());
//        }
//
//        //check valid tree -> no cycles
//        CycleFinder cf = new CycleFinder(g);
//        if(cf.containsCycle()) return false;
//
//
//        //check valid tree -> connectivity
//        ConnectivityAlgorithm ca = new ConnectivityAlgorithm(g);
//        if(ca.countConnectedComponents() != 1) return false;
//
//
//        return true;
//    }
//}
