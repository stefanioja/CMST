package org.example;

import com.gurobi.gurobi.GRBEnv;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBModel;
import com.gurobi.gurobi.GRBVar;

public class ModelDTO {
    public GRBEnv env;
    public GRBModel model;
    public GRBVar[][] x;
    public final GRBVar[][] y;

    public ModelDTO(GRBEnv env, GRBModel model, GRBVar[][] x, GRBVar[][] y) {
        this.env = env;
        this.model = model;
        this.x = x;
        this.y = y;
    }

    public GRBVar[][] getX() {
        return x;
    }

    public GRBVar[][] getY() {
        return y;
    }


    public GRBModel getModel() {
        return model;
    }

    public void optimize() throws GRBException {
        model.optimize();
    }

    public void dispose() {
        try {
            model.dispose();
            env.dispose();
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }
}