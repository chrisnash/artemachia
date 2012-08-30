package com.battlespace.domain.player;

import java.util.List;

import com.battlespace.domain.AbsoluteStat;
import com.battlespace.domain.AbstractPlayerPower;
import com.battlespace.domain.PlayerPower;
import com.battlespace.domain.PlayerShipInstance;

public class BoostWeapons extends AbstractPlayerPower
{
    String size;
    double percent;
    
    public BoostWeapons(List<String> params)
    {
        this.size = params.get(0);
        this.percent=Double.valueOf(params.get(1));
    }

    @Override
    public PlayerShipInstance upgrade(PlayerShipInstance psi) throws Exception
    {
        if(!psi.size.equals(size)) return psi;
        return new PlayerShipInstance(psi.name, AbsoluteStat.enhance(psi.torpedoes,percent), AbsoluteStat.enhance(psi.plasma,percent), psi.torpedoShield, psi.plasmaShield, psi.durability, psi.domination, psi.speed, psi.units);
    }
}
