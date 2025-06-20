package org.example;

import com.gurobi.gurobi.*;
import org.graph4j.Graph;
import org.graph4j.util.EdgeSet;

import java.util.Arrays;

public class CMSTMILPModel {
    private final Graph graph;
    private final int    Q;
    private final EdgeSet edgeSet;

    public CMSTMILPModel(Graph graph, int k) {
        this.graph   = graph;
        this.Q       = k;
        this.edgeSet = new EdgeSet(graph, graph.numVertices() - 1);
    }

    public void optimize() {
        try {
            int n = graph.numVertices() - 1;
            double[] d = new double[n + 1];
            Arrays.fill(d, 1.0);
            d[0] = 0.0;

            double[][] w = new double[n + 1][n + 1];
            for (int i = 0; i <= n; i++) {
                for (int j = 0; j <= n; j++) {
                    if (i == j) continue;
                    w[i][j] = graph.edge(i, j).weight();
                }
            }

            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "gurobi.log");
            env.set(GRB.IntParam.LogToConsole, 0);
            env.start();
            ;
            GRBModel model = new GRBModel(env);
            model.set(GRB.StringAttr.ModelName, "CapacitatedTree");

            GRBVar[][] x = new GRBVar[n + 1][n + 1];
            GRBVar[][] y = new GRBVar[n + 1][n + 1];

            for (int i = 0; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (i == j) continue;
                    x[i][j] = model.addVar(0.0, 1.0, w[i][j], GRB.BINARY,
                            "x_" + i + "_" + j);
                    y[i][j] = model.addVar(0.0, Q,   0.0,      GRB.CONTINUOUS,
                            "y_" + i + "_" + j);
                }
            }

            for (int j = 1; j <= n; j++) {
                GRBLinExpr expr = new GRBLinExpr();
                for (int i = 0; i <= n; i++)
                    if (i != j) expr.addTerm(1.0, x[i][j]);
                model.addConstr(expr, GRB.EQUAL, 1.0, "one_parent_" + j);
            }


            for (int j = 1; j <= n; j++) {
                GRBLinExpr flow = new GRBLinExpr();
                for (int i = 0; i <= n; i++)
                    if (i != j) flow.addTerm(1.0, y[i][j]);
                for (int i = 1; i <= n; i++)
                    if (i != j) flow.addTerm(-1.0, y[j][i]);
                model.addConstr(flow, GRB.EQUAL, 1.0, "flow_" + j);
            }


            for (int i = 0; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (i == j) continue;

                    GRBLinExpr exprMin = new GRBLinExpr();
                    exprMin.addTerm(1.0, y[i][j]);
                    exprMin.addTerm(-1.0, x[i][j]);
                    model.addConstr(exprMin, GRB.GREATER_EQUAL, 0.0,
                            "minFlow_" + i + "_" + j);

                    GRBLinExpr exprMax = new GRBLinExpr();
                    exprMax.addTerm(1.0, y[i][j]);

                    GRBLinExpr exprMaxK = new GRBLinExpr();
                    exprMaxK.addTerm(Q - d[i], x[i][j]);

                    model.addConstr(exprMax, GRB.LESS_EQUAL, exprMaxK,
                            "cap_" + i + "_" + j);
                }
            }
            model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
            model.optimize();

            for (int i = 0; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (i != j && x[i][j] != null && x[i][j].get(GRB.DoubleAttr.X) > 0.5) {
                        edgeSet.add(i, j);
                    }
                }
            }

            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.err.println("Eroare Gurobi: " + e.getMessage());
        }
    }

    public EdgeSet getTreeEdges() {
        return edgeSet;
    }
}
