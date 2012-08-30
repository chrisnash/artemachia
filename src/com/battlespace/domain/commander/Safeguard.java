package com.battlespace.domain.commander;

import java.util.List;

import com.battlespace.domain.AbsoluteStat;
import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.PlayerShipInstance;

public class Safeguard implements CommanderPower
{
    double percent;
    
    public Safeguard(List<String> params)
    {
        this.percent = Double.valueOf(params.get(0));
    }
    
    @Override
    public PlayerShipInstance upgrade(PlayerShipInstance psi) throws Exception
    {
        return new PlayerShipInstance(psi.name, AbsoluteStat.enhance(psi.torpedoes, percent), psi.plasma, psi.torpedoShield, psi.plasmaShield, psi.durability, psi.domination, psi.speed, psi.units);
    }

}
