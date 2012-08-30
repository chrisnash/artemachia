package com.battlespace.domain;

public interface Upgrader
{
    PlayerShipInstance upgrade(PlayerShipInstance psi) throws Exception;
}
