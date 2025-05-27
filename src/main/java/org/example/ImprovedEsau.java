package org.example;

import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.UnionFind;

import java.util.*;
/*
versiune mai veche, baza esauwilliams, si aici avem probleme de gestiune a componentelor, a nu se folosi
 */
public class ImprovedEsau extends CapacitatedMinimumSpanningTreeBase {

    private final Map<Integer, String> map = new HashMap<>();

    public ImprovedEsau(Graph g, int root, int capacity, int[] costs) {
        super(g, root, capacity, costs);
    }

    @Override
    protected void compute() {
        int n = graph.numVertices();
        UnionFind uf = new UnionFind(n);
        this.treeEdges = new EdgeSet(graph, n - 1);

        int[] subtreeCosts = new int[n];
        int[] closest = new int[n];
        double[] tradeoffs = new double[n];
        double[] gates = new double[n];

        for (int i : graph.vertices()) {
            if (i == root) continue;
            int id = graph.indexOf(i);
            subtreeCosts[id] = costs[id];
            gates[id] = graph.edge(i, root).weight();
        }

        for (int i : graph.vertices()) {
            if (i == root) continue;
            int id = graph.indexOf(i);
            closest[id] = getClosest(graph, uf, root, i, subtreeCosts, capacity);
            if (closest[id] != -1) {
                Edge e = graph.edge(i, closest[id]);
                tradeoffs[id] = gates[id] - e.weight();
            } else {
                tradeoffs[id] = -1;
            }
        }

        int iteration = 0;
        boolean changed;

        do {
            changed = false;

            double tMax = Double.NEGATIVE_INFINITY;
            Edge bestEdge = null;
            int bestRootI = -1, bestRootJ = -1;

            for (int i : graph.vertices()) {
                if (i == root) continue;
                int idI = graph.indexOf(i);
                double tradeoff = tradeoffs[idI];
                int j = closest[idI];

                if (j == -1) continue;

                int idJ = graph.indexOf(j);
                int rootI = uf.find(idI);
                int rootJ = uf.find(idJ);

                if (tradeoff > tMax && tradeoff > 0 && rootI != rootJ &&
                        (subtreeCosts[rootI] + subtreeCosts[rootJ] <= capacity)) {
                    tMax = tradeoff;
                    bestEdge = graph.edge(i, j);
                    bestRootI = rootI;
                    bestRootJ = rootJ;
                }
            }

            if (bestEdge != null) {

                this.treeEdges.remove(graph.edge(bestEdge.source(), root));
                this.treeEdges.add(bestEdge);

                uf.union(bestRootI, bestRootJ);

                int u = bestEdge.source();
                int v = bestEdge.target();
                int idU = graph.indexOf(u);
                int idV = graph.indexOf(v);

                int newCost = subtreeCosts[idU] + subtreeCosts[idV];
                subtreeCosts[idU] = newCost;
                subtreeCosts[idV] = newCost;

                double newGate = Math.min(gates[idU], gates[idV]);
                gates[idU] = newGate;
                gates[idV] = newGate;

                for (int id : new int[]{idU, idV}) {
                    int node = graph.vertexAt(id);
                    int close = getClosest(graph, uf, root, node, subtreeCosts, capacity);
                    closest[id] = close;
                    if (close != -1) {
                        Edge e = graph.edge(node, close);
                        tradeoffs[id] = gates[id] - e.weight();
                    } else {
                        tradeoffs[id] = -1;
                    }
                }

                changed = true;
            } else {
            }

        } while (changed);
        Map<Integer, List<Integer>> components = new HashMap<>();
        for (int i : graph.vertices()) {
            if (i == root) continue;
            int parent = uf.find(graph.indexOf(i));
            components.computeIfAbsent(parent, k -> new ArrayList<>()).add(i);
        }

        for (List<Integer> component : components.values()) {
            double minWeight = Double.MAX_VALUE;
            Edge bestEdge = null;
            for (int i : component) {
                Edge e = graph.edge(i, root);
                if (e != null && e.weight() < minWeight) {
                    minWeight = e.weight();
                    bestEdge = e;
                }
            }
            if (bestEdge != null) {
                treeEdges.add(bestEdge);
            } else {
            }
        }

    }

    private int getClosest(Graph g, UnionFind uf, int root, int vertex, int[] subtreeCosts, int capacity) {
        int uIdx = graph.indexOf(vertex);
        double minWeight = Double.MAX_VALUE;
        int best = -1;

        for (Edge e : g.edgesOf(vertex)) {
            int v = e.target();
            if (v == root) continue;

            int vIdx = graph.indexOf(v);
            if (uf.find(uIdx) == uf.find(vIdx)) continue;
            if (subtreeCosts[uIdx] + subtreeCosts[vIdx] > capacity) continue;

            if (e.weight() < minWeight) {
                minWeight = e.weight();
                best = v;
            }
        }

        return best;
    }

}


