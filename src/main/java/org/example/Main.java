package org.example;

import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.jgrapht.alg.interfaces.CapacitatedSpanningTreeAlgorithm;
import org.jgrapht.alg.interfaces.CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree;
import org.jgrapht.alg.spanning.EsauWilliamsCapacitatedMinimumSpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;
/*
cod de test pentru algoritm
 */
public class Main {

    public static void main(String[] args) {
        int[] sizes = {5, 10, 20, 50, 100, 200, 500, 1000, 2000}; // poți extinde
        //int[] sizes = {5, 10, 20, 50};
        int trialsPerSize = 3;

        for (int size : sizes) {
            double timeGraph4j = 0;
            double weightGraph4j = 0;
            double timeJGraphT = 0;
            double weightJGraphT = 0;

            for (int trial = 1; trial <= trialsPerSize; trial++) {
                GraphTrialData data = generateGraph(size, 0.5);
                BenchmarkResult r1 = runGraph4j(data);
                BenchmarkResult r2 = runJGraphT(data);

                timeGraph4j += r1.timeMs;
                weightGraph4j += r1.totalWeight;
                timeJGraphT += r2.timeMs;
                weightJGraphT += r2.totalWeight;

                System.out.printf("N=%d Trial=%d | Graph4j: %.2fms, W=%.2f | JGraphT: %.2fms, W=%.2f\n",
                        size, trial, r1.timeMs, r1.totalWeight, r2.timeMs, r2.totalWeight);
            }

            System.out.printf(">> N=%d | Avg G4J: %.2fms, W=%.2f | Avg JGT: %.2fms, W=%.2f\n\n",
                    size,
                    timeGraph4j / trialsPerSize, weightGraph4j / trialsPerSize,
                    timeJGraphT / trialsPerSize, weightJGraphT / trialsPerSize);
        }
    }

    private static GraphTrialData generateGraph(int n, double density) {
        Random rand = new Random();
        int root = 0;
        int capacity = 10;

        // Graph4j
        Graph g4j = GraphBuilder.numVertices(n).buildGraph();
        double[][] weights = new double[n][n];

        for (int i = 1; i < n; i++) {
            double w = 1 + rand.nextInt(10);
            g4j.addEdge(root, i, w);
            weights[root][i] = weights[i][root] = w;
        }

        for (int i = 1; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (rand.nextDouble() < density) {
                    double w = 1 + rand.nextInt(20);
                    g4j.addEdge(i, j, w);
                    weights[i][j] = weights[j][i] = w;
                }
            }
        }

        int[] costs = new int[n];
        Map<Integer, Double> costMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int c = 1 + rand.nextInt(3);
            costs[i] = c;
            costMap.put(i, (double) c);
        }

        return new GraphTrialData(g4j, weights, costs, costMap, root, capacity, n);
    }

    private static BenchmarkResult runGraph4j(GraphTrialData data) {
        long start = System.nanoTime();
        EsauWilliams ew = new EsauWilliams(data.graph4j, data.root, data.capacity, data.costs);
        ew.compute();
        long end = System.nanoTime();

        int num_of_edges = 0;
//        for(Edge e: ew.getEdges()) {
//            //System.out.println(e);
//            num_of_edges += 1;
//        }

        System.out.println("Total number of edges: " + num_of_edges);
        return new BenchmarkResult((end - start) / 1e6, ew.getWeight());
    }

    private static BenchmarkResult runJGraphT(GraphTrialData data) {
        SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        for (int i = 0; i < data.n; i++) {
            graph.addVertex(i);
        }

        for (int i = 0; i < data.n; i++) {
            for (int j = i + 1; j < data.n; j++) {
                if (data.weights[i][j] > 0) {
                    DefaultWeightedEdge e = graph.addEdge(i, j);
                    graph.setEdgeWeight(e, data.weights[i][j]);
                }
            }
        }

        long start = System.nanoTime();

        CapacitatedSpanningTreeAlgorithm<Integer, DefaultWeightedEdge> cmst =
                new EsauWilliamsCapacitatedMinimumSpanningTree<>(
                        graph, data.root, data.capacity, data.costMap, 1
                );

        CapacitatedSpanningTree<Integer, DefaultWeightedEdge> result = cmst.getCapacitatedSpanningTree();

        long end = System.nanoTime();

//        for(var e: result.getEdges()) {
//            System.out.println(e);
//        }
        return new BenchmarkResult((end - start) / 1e6, result.getWeight());
    }

    private static class BenchmarkResult {
        double timeMs;
        double totalWeight;

        BenchmarkResult(double timeMs, double totalWeight) {
            this.timeMs = timeMs;
            this.totalWeight = totalWeight;
        }
    }

    private static class GraphTrialData {
        Graph graph4j;
        double[][] weights;
        int[] costs;
        Map<Integer, Double> costMap;
        int root;
        int capacity;
        int n;

        GraphTrialData(Graph graph4j, double[][] weights, int[] costs,
                       Map<Integer, Double> costMap, int root, int capacity, int n) {
            this.graph4j = graph4j;
            this.weights = weights;
            this.costs = costs;
            this.costMap = costMap;
            this.root = root;
            this.capacity = capacity;
            this.n = n;
        }
    }
}
