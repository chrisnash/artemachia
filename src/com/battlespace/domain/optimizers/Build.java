package com.battlespace.domain.optimizers;

import java.util.List;

import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorState;

public class Build implements FitnessFunction
{
    double victoryThreshold;
    double maxScore;
    double timeScale;
    
    public Build(List<String> params)
    {
        this.victoryThreshold = Double.valueOf(params.get(0));
        this.maxScore = Double.valueOf(params.get(1));
        this.timeScale = Double.valueOf(params.get(2));
    }
    
    @Override
    public double getFitness(SimulatorState v)
    {
        double q = v.getStat("victoryPercent") - victoryThreshold;
        if(q>0)
        {
            double rt = v.getStat("buildTime");
            q = maxScore - (rt/timeScale);
            if(q<0) q=0.0;
        }
        return q;
    }

}
