package com.battlespace.domain;

import java.util.Map;

public class EnemyShipInstance extends EnemyShip implements ShipInstance
{

    public EnemyShipInstance(String name, Map<String, Stat> torp,
            Map<String, Stat> plas, Stat torpShield, Stat plasShield, Stat dur,
            Stat dom, Stat speed, int units) throws Exception
    {
        super(name, torp, plas, torpShield, plasShield, dur, dom, speed, units);
        // TODO Auto-generated constructor stub
    }

}
