package org.example;

import org.graph4j.generators.EdgeWeightsGenerator;
import org.graph4j.generators.GraphGenerator;
import org.jgrapht.alg.spanning.EsauWilliamsCapacitatedMinimumSpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.openjdk.jmh.annotations.*;
import org.graph4j.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@Fork(value = 1)
public class CmstBenchmark {


    @Param({"150", "200", "230"})
    private int n;
    private int K;
    private int[] demands;
    private Map<Integer, Double> demandMap;
    private Graph g;

    @Setup(Level.Trial)
    public void setup() {
        demands = new int[n];
        Arrays.fill(demands, 1);

        Map<Integer, Double> demandMap = new HashMap<>();    // FIX-3: <Integer,Double>
        for (int i = 0;i < n;i++) demandMap.put(i, 1.0);


        //g = GraphGenerator.complete(n);
        g = GraphGenerator.randomGnp(n, 0.1);
        for(int i: g.vertices()){
            if(g.containsEdge(i, 0)) continue;
            g.addEdge(i, 0);
        }
        EdgeWeightsGenerator.randomIntegers(g, 1, 100);
    }

    @Benchmark
    public double ew() {
        var cmst = new EsauWilliamsOptimized(g, 0, (int)Math.ceil(0.1 * n), demands);
        return cmst.getWeight();
    }

//    @Benchmark
//    public double fastImpl() {
//        var cmst = new CMSTMILPModel(g, (int)Math.ceil(0.1 * n));
//        cmst.optimize();
//        return cmst.getTreeEdges().weight();
//    }

    @Benchmark
    public double jgt() {
        InstanceConverter ic = new InstanceConverter(g);
        var graph = ic.getJGraphT();

        EsauWilliamsCapacitatedMinimumSpanningTree<Integer, DefaultWeightedEdge> jgtAlg =
                new EsauWilliamsCapacitatedMinimumSpanningTree<>(
                        graph, 0, (int)Math.ceil(0.1 * n), demandMap, 1);

        return jgtAlg.getCapacitatedSpanningTree().getWeight();
    }
}
