package com.battlespace.domain;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SimulatorResults
{
    boolean victory;
    Deployment playerResult;
    Deployment enemyResult;
    
    String[] availableStats = new String[]{"victoryPercent", "shipLosses", "shipCount", "domination", "enemyKills", "enemyDamage", "replacementTime", "dataValue"};

    public SimulatorResults(boolean v, Deployment player, Deployment enemy)
    {
        this.victory = v;
        this.playerResult = player;
        this.enemyResult =enemy;
    }
    
    public String toString()
    {
        return (victory?"Victory":"Defeat") + "\n" + playerResult + "\n" + enemyResult;
    }

    public Map<String, Double> getAllStats() throws Exception
    {
        Map<String, Double> out = new HashMap<String, Double>();
        for(String stat : availableStats)
        {
            out.put(stat, Double.valueOf(getStat(stat)));
        }
        return out;
    }

    private double getStat(String stat) throws Exception
    {
        // needs reflection
        Method m = this.getClass().getDeclaredMethod(stat);
        Double d = (Double) m.invoke(this);
        return d.doubleValue();
    }
    
    private double victoryPercent()
    {
        return victory?100.0:0.0;
    }
    private double shipLosses()
    {
        return (double)playerResult.shipsLost();
    }
    private double shipCount()
    {
        return (double)playerResult.livingShipList().size();
    }
    private double domination()
    {
        return playerResult.getEffectiveDomination();
    }
    
    // ships lost and damage ratio need to carry from one round to the next
    private double enemyKills()
    {
        return (double)enemyResult.shipsLost();
    }
    private double enemyDamage()
    {
        return enemyResult.damageRatio();
    }
    
    private double replacementTime()
    {
        return (double)playerResult.replacementTime();
    }
    private double dataValue()
    {
        return enemyResult.dataValue();
    }
}
