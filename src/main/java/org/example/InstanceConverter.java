package org.example;

import com.gurobi.gurobi.*;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class InstanceConverter {
    private Graph graph;
    private int   capacity;

    public InstanceConverter(Graph graph){
        this.graph = graph;
    }

    public InstanceConverter(Graph graph, int capacity) {
        this.graph = graph;
        this.capacity = capacity;
    }

    public SimpleWeightedGraph<Integer, DefaultWeightedEdge> getJGraphT(){
        SimpleWeightedGraph<Integer, DefaultWeightedEdge> g =
                new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        for(int i: graph.vertices()){
            g.addVertex(i);
        }

        for(Edge e: graph.edges()){
            g.addEdge(e.source(), e.target());
            g.setEdgeWeight(e.source(), e.target(), e.weight());
        }

        return g;
    }
}
