package com.battlespace.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.battlespace.domain.Booster;
import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.DamageEntry;
import com.battlespace.domain.Deployment;
import com.battlespace.domain.EnemyShip;
import com.battlespace.domain.EnemyShipDatabase;
import com.battlespace.domain.EnemyShipInstance;
import com.battlespace.domain.FileData;
import com.battlespace.domain.Formation;
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.PlayerShipInstance;
import com.battlespace.domain.RangedStat;
import com.battlespace.domain.ShipInstance;
import com.battlespace.domain.Stat;
import com.battlespace.service.CommanderPowerService;
import com.battlespace.service.DataLoaderService;
import com.battlespace.service.FormationService;
import com.battlespace.service.PlayerShipDatabase;
import com.battlespace.service.PlayerSkillModifier;
import com.battlespace.service.StatFactory;

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
        String replayFile = "data/replays/" + "brutus_1.txt";
        
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
            PlayerShip psi = PlayerShipDatabase.lookup(attackShipName);
            
            // apply ship upgrades
            psi = psi.applyUpgrades( upgradeLevels.get(attackShipName) );

            // calculate boost percentages
            Booster booster = new Booster(psi.size);
            
            PlayerSkillModifier.upgrade(booster, militarySkill);
            commanderPower.upgrade(booster);
            booster.skillUpgrade(commanderBoost);
            
            psi = psi.applyBooster(booster);
            attackShips.add(psi.createInstance());
        }
        Deployment attackDeployment = attackFormation.deploy(attackShips);

        String defendFormationName = replay.get("defender");
        List<String> defendShipNames = replay.getList("defender_ships");
        
        List<EnemyShipInstance> defendShips = new LinkedList<EnemyShipInstance>();
        for(String defendShipName : defendShipNames)
        {
            defendShips.add(esd.instantiate(defendShipName));
        }
        Formation defendFormation = FormationService.get(defendFormationName);
        Deployment defendDeployment = defendFormation.deploy(defendShips);
        
        // attempt to load the battle stats.1.attacker, stats.1.defender, damage.1.attacker, damage.1.defender
        for(int turn=1;;turn++)
        {
            List<String> attackStats = replay.getList("stats."+turn+".attacker");
            if(attackStats == null)
            {
                break;
            }
            // allow for display rounding and make sure they're compatible with our calculations
            List<RangedStat> displayRange = RangedStat.statsFromDisplay(attackStats);
            List<Stat> realRange = attackDeployment.getStats();
            List<Stat> extractedAttackStats = RangedStat.merge(displayRange, realRange);
            
            // How to analyze defend stats. Obviously we need to do a display breakdown
            List<String> defendStats = replay.getList("stats."+turn+".defender");
            displayRange = RangedStat.statsFromDisplay(defendStats);
            
            // now the trick is as follows. Summarize deployment as a map of
            // ship name to effective ship count.
            // Then for each ship type, min is given by back calculation (assuming all other ships maxed)
            // and likewise for max
            Map<String, Double> deploymentSummary = defendDeployment.getSummary();
            Set<String> enemyNames = deploymentSummary.keySet();
            for(String treatFixed : enemyNames)
            {
                // sum the raw stats from the remaining ships
                List<Stat> others = RangedStat.createEmptyStats();
                for(String otherShipName : enemyNames)
                {
                    if(otherShipName.equals(treatFixed)) continue;
                    EnemyShip otherShip = esd.lookup(otherShipName);
                    List<Stat> stats = otherShip.getSummaryStats();
                    List<Stat> scaled = RangedStat.scale(stats, deploymentSummary.get(otherShipName));
                    others = RangedStat.sum(others, scaled);
                }
                // display range sub others what our ship might be.
                List<Stat> fixedStats = RangedStat.diff(displayRange, others);
                List<Stat> newStatEstimate = RangedStat.scale(fixedStats, 1.0 / deploymentSummary.get(treatFixed));
                EnemyShip fixedShip = esd.lookup(treatFixed);
                esd.refineSummaryStats(fixedShip, newStatEstimate);
            }
            
            List<String> attackDamage = replay.getList("damage."+turn+".attacker");
            List<DamageEntry> ad = DamageEntry.parseDamage(attackDamage, attackDeployment);
            
            List<String> defendDamage = replay.getList("damage."+turn+".defender");
            List<DamageEntry> dd = DamageEntry.parseDamage(defendDamage, attackDeployment);
       
            // update damage and ship counts
            int afl = attackDeployment.frontLine();
            for(int i=0;i<10;i++)
            {
                attackDeployment.updateDamage(i%5, afl+((i>=5)?1:0), ad.get(i));
            }
            int dfl = defendDeployment.frontLine();
            for(int i=0;i<10;i++)
            {
                defendDeployment.updateDamage(i%5, dfl+((i>=5)?1:0), dd.get(i));
            }
            // use ship count to refine durability estimates
            Collection<ShipInstance> defenders = defendDeployment.getAllShips();
            for(ShipInstance defender : defenders)
            {
                EnemyShip nme = (EnemyShip)defender.getParent();
                int total = nme.getUnits();
                int current = defender.getUnits();
                int removed = total - current;
                Stat damage = defender.getDamage();
                // estimate durability based on damage data
                // removed = damage/durability * total, rounded down
                // so removed <= damage/durability * total < removed+1
                // in other words durability <= damage*total/removed
                // and durability > damage*total/(removed+1)
                
                // of course, this is wrong for overkill :)
                double min = 0;
                if(current > 0) // you can't set a min if you wiped them out
                {
                    min = (damage.value(false)*total) / (removed+1);
                }
                double max = 10000.0;
                if(removed>0) // you can't know a max if you havent hurt anyone yet
                {
                    max = (damage.value(true) * total) / removed;
                }
                Stat refined = StatFactory.create(min, max);
                esd.refineDurability(nme, refined);
            }
        }
        
        esd.update();
    }
}
