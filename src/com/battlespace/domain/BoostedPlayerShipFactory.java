package com.battlespace.domain;

import java.util.HashMap;
import java.util.Map;

public class BoostedPlayerShipFactory implements ShipFactory
{
    Map<String, PlayerShip> registry = new HashMap<String, PlayerShip>();
    
    public void register(String name, PlayerShip ps)
    {
        registry.put(name, ps);
    }
    
    @Override
    public ShipInstance createShip(String name) throws Exception
    {
        PlayerShip ps = registry.get(name);
        return ps.createInstance();
    }

}
