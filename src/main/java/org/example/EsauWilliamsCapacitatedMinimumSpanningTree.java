package org.example;

import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.UnionFind;
import java.util.*;
import java.util.stream.Collectors;
/*
prima versiune, incercare de a folosit PriorityQueues
rapid, dar ramane fara heap, am abandonat ideea de n PriorityQueues, momentan
probleme cu gestionarea componentelor, a nu se folosi
 */
public class EsauWilliamsCapacitatedMinimumSpanningTree extends CapacitatedMinimumSpanningTreeBase {

    public EsauWilliamsCapacitatedMinimumSpanningTree(Graph g, int root, int capacity, int[] costs) {
        super(g, root, capacity, costs);
    }

    @Override
    protected void compute(){
        int n = graph.numVertices();
        UnionFind uf = new UnionFind(n);
        this.treeEdges = new EdgeSet(graph, n - 1);
        int[] subtreeCosts = new int[n];
        double[] gates = new double[n];
        Map<Integer, PriorityQueue<Edge>> edges = new HashMap<>();

        for(int i: graph.vertices()) {
            PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(Edge::weight));
            for (Edge e : graph.edgesOf(i)) {
                pq.add(e);
            }

            edges.put(i, pq);
        }

        for(int i: graph.vertices()) {
            subtreeCosts[graph.indexOf(i)] = costs[graph.indexOf(i)];
            if(i == root) continue;
            gates[graph.indexOf(i)] = graph.edge(i, root).weight();
        }

        boolean changed;
        PriorityQueue<Edge> bestPQ;
        int rootI=0, rootJ=0;
        do {
            changed = false;
            bestPQ = null;
            double tMax = Double.NEGATIVE_INFINITY;
            Edge bestEdge = null;

            for (int i: graph.vertices()) {
                if (i == root) continue;
                PriorityQueue<Edge> pq = edges.get(i);

                while (!pq.isEmpty()) {
                    Edge candidate = pq.peek();
                    int j = candidate.target();

                    int root1 = uf.find(graph.indexOf(i));
                    int root2 = uf.find(graph.indexOf(j));

                    if (j == i || j == root || root1 == root2){
                        pq.poll();
                        continue;
                    }

                    double tradeoff = gates[graph.indexOf(i)] - candidate.weight();

                    if (tradeoff > tMax && tradeoff >= 0) {
                        int costI = subtreeCosts[graph.indexOf(i)];
                        int costJ = subtreeCosts[graph.indexOf(j)];
                        if (costI + costJ <= capacity) {
                            rootI = root1;
                            rootJ = root2;

                            tMax = tradeoff;
                            bestEdge = candidate;
                            bestPQ = pq;
                        }else{
                            pq.poll();
                            changed = true;
                        }
                    }
                    break;
                }
            }

            if (bestEdge != null) {
                this.treeEdges.remove(graph.edge(bestEdge.source(), root));
                this.treeEdges.add(bestEdge);

                if(bestPQ != null) bestPQ.poll();
                uf.union(rootI, rootJ);

                int u = bestEdge.source();
                int v = bestEdge.target();
                int newSize = subtreeCosts[graph.indexOf(u)] + subtreeCosts[graph.indexOf(v)];
                subtreeCosts[graph.indexOf(u)] = newSize;
                subtreeCosts[graph.indexOf(v)] = newSize;

                double newGate = Math.min(gates[graph.indexOf(u)], gates[graph.indexOf(v)]);
                gates[graph.indexOf(u)] = newGate;
                gates[graph.indexOf(v)] = newGate;

                changed = true;
            }
        } while (changed);

        Map<Integer, List<Integer>> components = new HashMap<>();
        for (int i: graph.vertices()) {
            if (i == root) continue;

            int parent = uf.find(graph.indexOf(i));
            List<Integer> subTree = components.computeIfAbsent(parent, key -> new ArrayList<>());
            subTree.add(i);
            components.putIfAbsent(parent, subTree);
        }

        for (List<Integer> component : components.values()) {
            double minWeight = Double.MAX_VALUE;
            Edge bestEdge = null;
            for (int i : component) {
                Edge e = graph.edge(i, root);
                if (e != null && e.weight() < minWeight) {
                    minWeight = e.weight();
                    bestEdge = e;
                    break;
                }
            }
            if (bestEdge != null) {
                treeEdges.add(bestEdge);
            } else {
               //err
            }
        }
        //done
    }
}
