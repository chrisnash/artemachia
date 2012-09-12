package com.battlespace.domain;

import java.util.HashMap;
import java.util.Map;

import com.battlespace.service.PlayerShipDatabase;

public class PlayerShip extends AbstractShip
{
    ShipEnhancement[] enhancements = new ShipEnhancement[4];
    int buildTime;
    
    public PlayerShip(String name, Map<String, Stat> torp,
            Map<String, Stat> plas, Stat torpShield, Stat plasShield, Stat dur,
            Stat dom, Stat speed, int units, int buildTime) throws Exception
    {
        super(name, torp, plas, torpShield, plasShield, dur, dom, speed, units);
        this.buildTime = buildTime;
    }

    public PlayerShipInstance createInstance() throws Exception
    {
        return new PlayerShipInstance(this);
    }
    
    public PlayerShip skillUpgrade(double commanderBoost) throws Exception
    {
        return new PlayerShip(name, AbsoluteStat.enhance(torpedoes, commanderBoost), AbsoluteStat.enhance(plasma, commanderBoost),
                torpedoShield, plasmaShield, durability, domination, speed, units, buildTime);                
    }
    
    public PlayerShip applyUpgrades(int[] is, boolean required) throws Exception
    {
        double[] in = flatten();
        for(int i=0;i<4;i++)
        {
            double[] mods = new double[AbstractShip.FLATTEN_SIZE];
            if(enhancements[i]==null)
            {
                System.out.println("WARNING: missing enhancement file for " + name);
                if(required) throw new UnsupportedOperationException();
            }
            else
            {
                mods = enhancements[i].flatten();                
            }
            int m = is[i];
            for(int j=0;j<AbstractShip.FLATTEN_SIZE;j++)
            {
                in[j] += mods[j]*m;
            }
        }
        
        Map<String, Stat> t = new HashMap<String, Stat>();
        t.put("S", new AbsoluteStat(in[0]));
        t.put("M", new AbsoluteStat(in[1]));
        t.put("L", new AbsoluteStat(in[2]));
        Map<String, Stat> p = new HashMap<String, Stat>();
        p.put("S", new AbsoluteStat(in[3]));
        p.put("M", new AbsoluteStat(in[4]));
        p.put("L", new AbsoluteStat(in[5]));
        
        return new PlayerShip(name, t, p, new AbsoluteStat(in[6]), new AbsoluteStat(in[7]),
                new AbsoluteStat(in[8]), new AbsoluteStat(in[9]), new AbsoluteStat(in[10]), units, buildTime);

    }



    public void registerEnhancement(int i, ShipEnhancement se)
    {
        enhancements[i] = se;
    }

    public PlayerShip applyBooster(Booster b) throws Exception
    {
        double[] in = flatten();
        for(int j=0;j<AbstractShip.FLATTEN_SIZE;j++)
        {
            in[j] *= (100.0 + b.boosts[j])/100.0;
        }
        
        Map<String, Stat> t = new HashMap<String, Stat>();
        t.put("S", new AbsoluteStat(in[0]));
        t.put("M", new AbsoluteStat(in[1]));
        t.put("L", new AbsoluteStat(in[2]));
        Map<String, Stat> p = new HashMap<String, Stat>();
        p.put("S", new AbsoluteStat(in[3]));
        p.put("M", new AbsoluteStat(in[4]));
        p.put("L", new AbsoluteStat(in[5]));
        
        return new PlayerShip(name, t, p, new AbsoluteStat(in[6]), new AbsoluteStat(in[7]),
                new AbsoluteStat(in[8]), new AbsoluteStat(in[9]), new AbsoluteStat(in[10]), units, buildTime);
    }

    @Override
    public int getReplacementTime()
    {
        return buildTime;
    }
}
