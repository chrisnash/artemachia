package com.battlespace.domain;

import java.util.Map;

public class PlayerShip extends AbstractShip
{
    ShipEnhancement[] enhancements = new ShipEnhancement[4];
    
    public PlayerShip(String name, Map<String, Stat> torp,
            Map<String, Stat> plas, Stat torpShield, Stat plasShield, Stat dur,
            Stat dom, Stat speed, int units) throws Exception
    {
        super(name, torp, plas, torpShield, plasShield, dur, dom, speed, units);
    }

    public PlayerShipInstance createInstance() throws Exception
    {
        return new PlayerShipInstance(name, torpedoes, plasma, torpedoShield, plasmaShield, durability, domination, speed, units);
    }

    public void registerEnhancement(int i, ShipEnhancement se)
    {
        enhancements[i] = se;
    }

}
