package com.battlespace.strategy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;

public class TwoTierAttackStrategy implements AttackStrategy
{
    public double secondPercent;
    
    public TwoTierAttackStrategy(List<String> params)
    {
        this.secondPercent = Double.valueOf(params.get(0));
    }
    
    // basic attack plan is two tiers
    // first tier is closest ships, counting second line as +1, row offset as +1
    // second tier is ships that are closest+1
    public AttackPlan getAttackPlan(Deployment attackDeployment, Deployment defendDeployment)
    {
        List<Coordinate> attackers = attackDeployment.livingShipList();
        List<Coordinate> defenders = defendDeployment.vulnerableShipList();
        int fl = defendDeployment.frontLine();
      
        Map<Coordinate, TwoTierAttackElement> elements = new HashMap<Coordinate, TwoTierAttackElement>();
        
        for(Coordinate attacker : attackers)
        {
            SortedMap<Integer, List<Coordinate>> tiers = new TreeMap<Integer, List<Coordinate>>();
            
            for(Coordinate defender : defenders)
            {
                int ro = attacker.r - defender.r;
                if(ro<0) ro=-ro;
                if(defender.c != fl) ro++;
                Integer o = Integer.valueOf(ro);
                List<Coordinate> l = tiers.get(o);
                if(l==null)
                {
                    l = new LinkedList<Coordinate>();
                    tiers.put(o,l);
                }
                l.add(defender);
            }
            // build the attack element. It's the first map element, and the second, if it exists
            // AND is in the next line
            Iterator<Map.Entry<Integer, List<Coordinate>>> it = tiers.entrySet().iterator();
            Map.Entry<Integer, List<Coordinate>> firstTier = it.next();
            Map.Entry<Integer, List<Coordinate>> secondTier = null;
            if(it.hasNext())
            {
                Map.Entry<Integer, List<Coordinate>> possible = it.next();
                if(possible.getKey().intValue() == firstTier.getKey().intValue()+1)
                {
                    secondTier = possible;
                }
            }
            TwoTierAttackElement e = new TwoTierAttackElement(firstTier.getValue(), (secondTier!=null)?secondTier.getValue():null);
            elements.put(attacker, e);
        }
        
        return new TwoTierAttackPlan(elements, this);
    }
}
