package com.battlespace.main.parsers;

import java.util.List;
import java.util.Set;

import com.battlespace.domain.BoostedPlayerShipFactory;
import com.battlespace.domain.Booster;
import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.FileData;
import com.battlespace.domain.PlayerShip;
import com.battlespace.service.CommanderPowerService;
import com.battlespace.service.PlayerShipDatabase;
import com.battlespace.service.PlayerSkillModifier;

public class ParsedCommander
{
    public String playerFormation;
    public int commanderSkill;
    public double commanderBoost;
    public int commanderLuck;
    public double commanderCritical;
    public CommanderPower commanderPower;
    
    public ParsedCommander(FileData config, String arg) throws Exception
    {
        // parameters
        // param 1 is player formation,(commander),(luck),(skill)
        //System.out.println(args[0]);
        String[] playerArgs = arg.split(",");
        playerFormation = playerArgs[0];

        commanderSkill = 0;
        if(playerArgs.length > 1)
        {
            commanderSkill = Integer.valueOf(playerArgs[1]);
        }
        commanderBoost = commanderSkill/Double.valueOf(config.get("skill_factor"));
        
        commanderLuck = 0;
        if(playerArgs.length > 2)
        {
            commanderLuck = Integer.valueOf(playerArgs[2]);
        }
        commanderCritical = commanderLuck/Double.valueOf(config.get("luck_factor"));
        
        commanderPower = null;
        if(playerArgs.length>3)
        {
            commanderPower = CommanderPowerService.get(playerArgs[3]);
        }

    }

    public BoostedPlayerShipFactory createShipFactory(
            FileData playerData, List<String> availableShips) throws Exception
    {
        BoostedPlayerShipFactory bpsf = new BoostedPlayerShipFactory();
        int militarySkill = playerData.getInt("military.skill", 0);
        int unionArmor = playerData.getInt("union.armor", 0);
        
        Set<String> keys = playerData.getKeys();
        for(String key : keys)
        {
            // will eventually have to check for valid ship keys
            if(!key.contains("."))      // all others contain a dot
            {
                // perfom boost calculations on this ship
                PlayerShip psi = PlayerShipDatabase.lookup(key);
                // get the ug as an array of int
                int[] ug = new int[4];
                String ugString = playerData.get(key);
                for(int i=0;i<4;i++)
                {
                    ug[i] = ugString.codePointAt(i) - '0';
                }
                psi = psi.applyUpgrades( ug, false);    // allow sim to run even with missing files
                Booster booster = new Booster(psi.size);
                PlayerSkillModifier.upgrade(booster, militarySkill);
                if(commanderPower!=null)
                {
                    commanderPower.upgrade(booster);
                }
                booster.skillUpgrade(commanderBoost);
                booster.add(new double[]{0,0,0, 0,0,0, 0,0, unionArmor*2.0,0, 0});
                psi = psi.applyBooster(booster);
                
                bpsf.register(key, psi);
                
                // and add it to the list of ships the GA can use
                availableShips.add(key);
            }
        }
        return bpsf;
    }

}
