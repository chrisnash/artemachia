package com.battlespace.strategy;

import java.util.Collection;
import java.util.SortedSet;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;
import com.battlespace.domain.ShipInstance;
import com.battlespace.service.Roller;

// the rolling attack processor will process the attacks one at a time.
// This may mean a defender gets obliterated and removed from consideration
public class RollingAttackProcessor implements AttackProcessor
{
    public void process(AttackPlan plan, Deployment defenders, Roller rng, double multiplier) throws Exception
    {
        RollingAttackPlan rap = (RollingAttackPlan)plan;
        SortedSet<Coordinate> attackerOrder = rap.getAttackerOrder();
        for(Coordinate attacker : attackerOrder)
        {
            Collection<Coordinate> survivors = defenders.vulnerableShipList();
            Coordinate target = rap.selectSingleAttack(attacker, survivors, rng);
            // fire on that target
            if(target!=null)    // there may not be one left
            {
                ShipInstance si = defenders.getLivingShip(target.r, target.c);
                double damage = rap.computeDamage(attacker, si, multiplier);
                si.setDamage( si.getDamage().value() + damage);     // this might be enough to kill the guy.
            }
        }
    }
}
