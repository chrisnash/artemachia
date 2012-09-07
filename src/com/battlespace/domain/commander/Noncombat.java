package com.battlespace.domain.commander;

import java.util.List;

import com.battlespace.domain.AbsoluteStat;
import com.battlespace.domain.Booster;
import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.PlayerShipInstance;

public class Noncombat implements CommanderPower
{
    public Noncombat(List<String> params)
    {
    }
    
    @Override
    public void upgrade(Booster b) throws Exception
    {
    }
}
