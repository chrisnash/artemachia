package com.battlespace.strategy;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;
import com.battlespace.domain.ShipInstance;
import com.battlespace.domain.Stat;

public class HalifixAttackStrategy implements AttackStrategy
{
    public double secondPercent;
    public int rowWeight;
    public AttackProcessor processor = new RollingAttackProcessor();
    
    public HalifixAttackStrategy(List<String> params)
    {
        this.secondPercent = Double.valueOf(params.get(0));
        this.rowWeight = Integer.valueOf(params.get(1));
    }
    
    class HalifixOrder implements Comparator<Coordinate>
    {
        @Override
        public int compare(Coordinate first, Coordinate second)
        {
            // lower columns first
            if(first.c != second.c)
            {
                return first.c - second.c;
            }
            return first.r - second.r;
        }   
    }
    // basic attack plan is two tiers
    // first tier is closest ships, counting second line as +1, row offset as +1
    // second tier is ships that are closest+1
    public AttackPlan getAttackPlan(Deployment attackDeployment, Deployment defendDeployment) throws Exception
    {
        List<Coordinate> attackers = attackDeployment.livingShipList();
        List<Coordinate> defenders = defendDeployment.vulnerableShipList();
        int fl = defendDeployment.frontLine();
        
        // How Hali's method works.
        // attacking ships attack IN ORDER, front to back, top to bottom.
        // attacks are tiered (closest, next closest, etc etc)
        // if an earlier attacker destroys a defender, then later attackers don't aim at the dead guy.
        
        // the attack plan builds the attackers in order, each with a List of tiers to consider.
        // Note that the first guy just needs two tiers. Later guys might need more as backup if all
        // the other guys in front get killed before they get their shot.
        
        SortedSet<Coordinate> sortedAttackers = new TreeSet<Coordinate>(new HalifixOrder());
        Map<Coordinate, List<List<Coordinate>>> storage = new HashMap<Coordinate, List<List<Coordinate>>>();
        Map<Coordinate, List<Stat>> stats = new HashMap<Coordinate, List<Stat>>();
        
        sortedAttackers.addAll(attackers);
        
        int attackerIndex = 0;
        for(Coordinate attacker : sortedAttackers)
        {
            Map<Integer, List<Coordinate>> tiers = new HashMap<Integer, List<Coordinate>>();
            for(Coordinate defender : defenders)
            {
                int ro = attacker.r - defender.r;
                if(ro<0) ro=-ro;
                ro *= rowWeight;
                if(defender.c != fl) ro++;
                List<Coordinate> l = tiers.get(ro);
                if(l==null)
                {
                    l = new LinkedList<Coordinate>();
                    tiers.put(ro,l);
                }
                l.add(defender);
            }
            // calculate which tiers you need to store. Note that attackerIndex ships might have already been
            // killed, so you need to gather enough tiers so that more ships than that are included, plus
            // one more backup tier. (tier index is 0-4*rowWeight+1).
            List< List<Coordinate> > cachedTiers = new LinkedList< List<Coordinate> >();
            int shipsSeen = 0;
            boolean exitAfterNext = false;
            for(int t=0;t<=4*rowWeight+1;t++)
            {
                List<Coordinate> tt = tiers.get(t);
                if(tt==null) continue;
                cachedTiers.add(tt);
                shipsSeen += tt.size();
                if(exitAfterNext) break;
                if(shipsSeen > attackerIndex) exitAfterNext = true;     // ie cache enough to get a ship, plu one extra
            }
            storage.put(attacker, cachedTiers);
            
            // make a copy of the effective stats
            ShipInstance si = attackDeployment.getLivingShip(attacker.r, attacker.c);
            List<Stat> es = si.getEffectiveStats();
            stats.put(attacker, es);
            
            attackerIndex++;
        }
      
        return new HalifixAttackPlan(sortedAttackers, storage, stats, this);
    }
    @Override
    public AttackProcessor getProcessor()
    {
        return processor;
    }
}
