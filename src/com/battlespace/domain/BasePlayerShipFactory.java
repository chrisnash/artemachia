package com.battlespace.domain;

import com.battlespace.service.PlayerShipDatabase;

public class BasePlayerShipFactory implements ShipFactory
{

    @Override
    public ShipInstance createShip(String name) throws Exception
    {
        return PlayerShipDatabase.instantiate(name);
    }

}
