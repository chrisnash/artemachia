package com.battlespace.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AbstractShip implements Ship
{
    public static final int FLATTEN_SIZE = 11;
    
    public String name;
    
    public String size;
    public Map<String, Stat> torpedoes;
    public Map<String, Stat> plasma;
    public Stat torpedoShield;
    public Stat plasmaShield;
    public Stat durability;
    public Stat domination;
    public Stat speed;
    public int units;
    
    String code;
    
    public AbstractShip(String name, Map<String, Stat> torp,
            Map<String, Stat> plas, Stat torpShield, Stat plasShield, Stat dur,
            Stat dom, Stat speed, int units) throws Exception
    {
        this.name = name;
        this.size = unitsToSize(units);
        this.torpedoes = torp;
        this.plasma = plas;
        this.torpedoShield = torpShield;
        this.plasmaShield = plasShield;
        this.durability = dur;
        this.domination = dom;
        this.speed = speed;
        this.units = units;
    }

    private static String unitsToSize(int units) throws Exception
    {
        if(units==6) return "S";
        if(units==3) return "M";
        if(units==1) return "L";
        throw new Exception("Inbvalid ship unit count: " + units);
    }
    
    public double[] flatten()
    {
        double[] out = new double[FLATTEN_SIZE];
        out[0] = torpedoes.get("S").value();
        out[1] = torpedoes.get("M").value();
        out[2] = torpedoes.get("L").value();
        out[3] = plasma.get("S").value();
        out[4] = plasma.get("M").value();
        out[5] = plasma.get("L").value();
        out[6] = torpedoShield.value();
        out[7] = plasmaShield.value();
        out[8] = durability.value();
        out[9] = domination.value();
        out[10] = speed.value();
        return out;
    }
    
    public String toString()
    {
        return name + " " + size + " " + torpedoes + " " + plasma + " " + torpedoShield + " " + plasmaShield + " " + durability + " " + domination + " " + speed + " " + units;
    }
    
    public int getUnits()
    {
        return units;
    }
    
    public List<Stat> getSummaryStats()
    {
        List<Stat> out = new LinkedList<Stat>();
        out.add(torpedoes.get("S"));
        out.add(torpedoes.get("M"));
        out.add(torpedoes.get("L"));
        out.add(plasma.get("S"));
        out.add(plasma.get("M"));
        out.add(plasma.get("L"));
        return out;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public Stat getDomination()
    {
        return domination;
    }
    
    public Stat getDurability()
    {
        return durability;
    }
    
    public List<Stat> getShieldStats()
    {
        List<Stat> out = new LinkedList<Stat>();
        out.add(torpedoShield);
        out.add(plasmaShield);
        return out;
    }
    
    public Stat getSpeed()
    {
        return speed;
    }
}
