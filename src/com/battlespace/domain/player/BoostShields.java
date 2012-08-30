package com.battlespace.domain.player;

import java.util.List;

import com.battlespace.domain.AbstractPlayerPower;
import com.battlespace.domain.PlayerPower;
import com.battlespace.domain.PlayerShipInstance;

public class BoostShields extends AbstractPlayerPower
{
    String size;
    double percent;
    
    public BoostShields(List<String> params)
    {
        this.size = params.get(0);
        this.percent=Double.valueOf(params.get(1));
    }

    @Override
    public PlayerShipInstance upgrade(PlayerShipInstance psi) throws Exception
    {
        if(!psi.size.equals(size)) return psi;
        return new PlayerShipInstance(psi.name, psi.torpedoes, psi.plasma, psi.torpedoShield.enhance(percent), psi.plasmaShield.enhance(percent), psi.durability, psi.domination, psi.speed, psi.units);

    }
}
