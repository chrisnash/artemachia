package com.battlespace.main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.Deployment;
import com.battlespace.domain.EnemyShipDatabase;
import com.battlespace.domain.EnemyShipInstance;
import com.battlespace.domain.FileData;
import com.battlespace.domain.Formation;
import com.battlespace.domain.PlayerShipInstance;
import com.battlespace.service.CommanderPowerService;
import com.battlespace.service.DataLoaderService;
import com.battlespace.service.FormationService;
import com.battlespace.service.PlayerShipDatabase;
import com.battlespace.service.PlayerSkillModifier;

public class ReplayRunner
{

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        FileData config = DataLoaderService.loadFile("conf/settings.txt");
        EnemyShipDatabase esd = EnemyShipDatabase.load();
        
        //String replayFile = "data/replays/" + args[0];
        String replayFile = "data/replays/" + "dvthalla_1.txt";
        
        FileData replay = DataLoaderService.loadFile(replayFile);

        int militarySkill = replay.getInt("attacker", 0);
        int commanderSkill = replay.getInt("attacker", 1);
        double commanderBoost = commanderSkill/Double.valueOf(config.get("skill_factor"));
        int commanderLuck = replay.getInt("attacker", 2);
        String commanderPowerName = replay.get("attacker", 3);
        CommanderPower commanderPower = CommanderPowerService.get(commanderPowerName);
        
        String attackFormationName = replay.get("attacker",4);
        
        Formation attackFormation = FormationService.get(attackFormationName);
        
        List<String> attackShipNames = replay.getList("attacker_ships");
        List<String> attackShipUpgrades = replay.getList("attacker_upgrades");
        Map<String, int[]> upgradeLevels = new HashMap<String, int[]>();
        for(String attackShipUpgrade : attackShipUpgrades)
        {
            String[] parts = attackShipUpgrade.split(":");
            int[] ul = new int[4];
            for(int i=0;i<4;i++)
            {
                ul[i] = parts[1].codePointAt(i) - '0';
            }
            upgradeLevels.put(parts[0], ul);
        }

        List<PlayerShipInstance> attackShips = new LinkedList<PlayerShipInstance>();
        
        for(String attackShipName : attackShipNames)
        {
            PlayerShipInstance psi = PlayerShipDatabase.instantiate(attackShipName);
            // apply ship upgrades
            psi = psi.applyUpgrades( upgradeLevels.get(attackShipName) );
            // apply player skill
            psi = PlayerSkillModifier.upgrade(psi, militarySkill);
            // apply commander skill
            psi = commanderPower.upgrade(psi);
            // apply commander stat
            psi = psi.skillUpgrade(commanderBoost);
            attackShips.add(psi);
        }
        //TODO
        //Deployment attackDeployment = attackFormation.deploy(attackShips);

        String defendFormationName = replay.get("defender");
        List<String> defendShipNames = replay.getList("defender_ships");
        
        List<EnemyShipInstance> defendShips = new LinkedList<EnemyShipInstance>();
        for(String defendShipName : defendShipNames)
        {
            //TODO
            //defendShips.add(esd.instantiate(defendShipName));
        }
        Formation defendFormation = FormationService.get(defendFormationName);
        //TODO
        //Deployment defendDeployment = defendFormation.deploy(defendShips);
        
        // attempt to load the battle stats.1.attacker, stats.1.defender, damage.1.attacker, damage.1.defender
        for(int turn=1;;turn++)
        {
            List<String> attackStats = replay.getList("stats."+turn+".attacker");
            if(attackStats == null)
            {
                break;
            }
            List<String> defendStats = replay.getList("stats."+turn+".defender");
            List<String> attackDamage = replay.getList("damage."+turn+".attacker");
            List<String> defendDamage = replay.getList("damage."+turn+".defender");
       
            // do whatever you need to do with this
        }
        
        //TODO
        //esd.update();
    }
}
