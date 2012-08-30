package com.battlespace.domain.player;

import java.util.List;

import com.battlespace.domain.AbstractPlayerPower;
import com.battlespace.domain.PlayerPower;
import com.battlespace.domain.PlayerShipInstance;

public class BoostSpeed extends AbstractPlayerPower
{
    double percent;
    
    public BoostSpeed(List<String> params)
    {
        this.percent = Double.valueOf(params.get(0));
    }
    
    @Override
    public PlayerShipInstance upgrade(PlayerShipInstance psi) throws Exception
    {
        return new PlayerShipInstance(psi.name, psi.torpedoes, psi.plasma, psi.torpedoShield, psi.plasmaShield, psi.durability, psi.domination, psi.speed.enhance(percent), psi.units);
    }
}
