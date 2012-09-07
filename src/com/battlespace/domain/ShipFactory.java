package com.battlespace.domain;

public interface ShipFactory
{
    ShipInstance createShip(String name) throws Exception;
}
