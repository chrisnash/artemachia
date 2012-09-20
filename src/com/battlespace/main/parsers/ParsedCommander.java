package com.battlespace.main.parsers;

import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.FileData;
import com.battlespace.service.CommanderPowerService;

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

}
