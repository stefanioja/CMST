package org.example;

import org.graph4j.Graph;
import org.graph4j.spanning.MinimumSpanningTreeBase;


public abstract class CapacitatedMinimumSpanningTreeBase extends MinimumSpanningTreeBase {
    protected int root;
    protected int capacity;
    protected int[] demands;

    public CapacitatedMinimumSpanningTreeBase(Graph graph, int root, int capacity, int[] demands) {
        super(graph);

        //checking params
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be a positive integer.");
        }

        if(!graph.containsVertex(root)) {
            throw new IllegalArgumentException("Root vertex doesn't exist.");
        }

        if (demands.length != graph.numVertices()) {
            throw new IllegalArgumentException("Cost array length must match number of vertices.");
        }

        for (int i = 0; i < demands.length; i++) {
            if (demands[i] > capacity) {
                throw new IllegalArgumentException("Node " + i + " has demand greater than capacity.");
            }
        }

        this.root = root;
        this.capacity = capacity;
        this.demands = demands;
    }
}