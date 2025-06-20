package org.example;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.generators.EdgeWeightsGenerator;
import org.graph4j.generators.GraphGenerator;

import java.util.Random;
//exemple mici
//jgrapgt
//random deja stiut
//gurobi
//neighbor iterator
public class Main {
    public static void main(String[] args){
        Graph g = GraphGenerator.complete(50);
        EdgeWeightsGenerator.randomIntegers(g, 1, 100);

        CMSTMILPModel cmst = new CMSTMILPModel(g, 3);
        cmst.optimize();
    }
}

