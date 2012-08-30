package com.battlespace.domain;

import java.util.HashMap;
import java.util.Map;

import com.battlespace.service.PlayerShipDatabase;

public class PlayerShipInstance extends PlayerShip implements ShipInstance
{

    public PlayerShipInstance(String name, Map<String, Stat> torp,
            Map<String, Stat> plas, Stat torpShield, Stat plasShield, Stat dur,
            Stat dom, Stat speed, int units) throws Exception
    {
        super(name, torp, plas, torpShield, plasShield, dur, dom, speed, units);
    }

    public PlayerShipInstance applyUpgrades(int[] is) throws Exception
    {
        PlayerShip ps = PlayerShipDatabase.lookup(name);
        double[] in = flatten();
        for(int i=0;i<4;i++)
        {
            int m = is[i];
            double[] mods = ps.enhancements[i].flatten();
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
        
        return new PlayerShipInstance(name, t, p, new AbsoluteStat(in[6]), new AbsoluteStat(in[7]), new AbsoluteStat(in[8]), new AbsoluteStat(in[9]), new AbsoluteStat(in[10]), units);

    }

    public PlayerShipInstance skillUpgrade(double commanderBoost) throws Exception
    {
        return new PlayerShipInstance(name, AbsoluteStat.enhance(torpedoes, commanderBoost), AbsoluteStat.enhance(plasma, commanderBoost),
                torpedoShield, plasmaShield, durability, domination, speed, units);
                
    }

}
