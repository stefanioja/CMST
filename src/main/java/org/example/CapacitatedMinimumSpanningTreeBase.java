package org.example;

import org.graph4j.Graph;
import org.graph4j.spanning.MinimumSpanningTreeBase;

/*
base class for cmst
 */
public abstract class CapacitatedMinimumSpanningTreeBase extends MinimumSpanningTreeBase {
    protected int root;
    protected int capacity;
    protected int[] costs;

    public CapacitatedMinimumSpanningTreeBase(Graph graph, int root, int capacity, int[] costs) {
        super(graph);
        this.root = root;
        this.capacity = capacity;
        this.costs = costs;
    }
}