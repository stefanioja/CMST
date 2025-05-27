package org.example;

import org.graph4j.*;

import java.util.*;
public class Test {
    public static void testExample() {

        //src: https://sites.pitt.edu/~dtipper/2110/CMST_example.pdf
        Graph g = GraphBuilder.empty().addClique(0, 1, 2, 3, 4, 5, 6)
                .buildGraph();

        g.setEdgeWeight(0, 1, 5);
        g.setEdgeWeight(0, 2, 6);
        g.setEdgeWeight(0, 3, 9);
        g.setEdgeWeight(0, 4, 10);
        g.setEdgeWeight(0, 5, 11);
        g.setEdgeWeight(0, 6, 15);

        g.setEdgeWeight(1, 2, 9);
        g.setEdgeWeight(1, 3, 6);
        g.setEdgeWeight(1, 4, 6);
        g.setEdgeWeight(1, 5, 8);
        g.setEdgeWeight(1, 6, 17);

        g.setEdgeWeight(2, 3, 7);
        g.setEdgeWeight(2, 4, 9);
        g.setEdgeWeight(2, 5, 8);
        g.setEdgeWeight(2, 6, 12);

        g.setEdgeWeight(3, 4, 10);
        g.setEdgeWeight(3, 5, 5);
        g.setEdgeWeight(3, 6, 11);

        g.setEdgeWeight(4, 5, 14);
        g.setEdgeWeight(4, 6, 9);

        g.setEdgeWeight(5, 6, 8);

        int[] costs = new int[7];
        for (int i = 0; i < 7; i++) costs[i] = i == 3 ? 2 : 1;
        CapacitatedMinimumSpanningTreeBase cmst = new EsauWilliams(g, 0, 3, costs);

        for (Edge e : cmst.getEdges()) {
            System.out.println(e);
        }
    }
};
