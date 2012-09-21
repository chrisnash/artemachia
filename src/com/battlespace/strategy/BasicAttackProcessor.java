package com.battlespace.strategy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;
import com.battlespace.domain.ShipInstance;
import com.battlespace.main.viewer.Viewer;
import com.battlespace.service.Roller;

// the basic attack processor will select all targets upfront and run the attacks in one
// batch. This tends to overkill.
public class BasicAttackProcessor implements AttackProcessor
{

    @Override
    public void process(AttackPlan plan, Deployment defenders, Roller rng,
            double multiplier, Viewer viewer) throws Exception
    {
        BasicAttackPlan bap = (BasicAttackPlan)plan;
        // this is who attacks who, precalculated
        Map<Coordinate, Coordinate> attack = bap.selectAttack(rng);
        Map<Coordinate, List<Coordinate>> attackersPerDefender = invertAttackers(attack);
        // process the incoming attacks AS IF they all happened at the same time on that ship
        for(Map.Entry<Coordinate, List<Coordinate>> e : attackersPerDefender.entrySet())
        {
            Coordinate target = e.getKey();
            List<Coordinate> attackers = e.getValue();

            ShipInstance si = defenders.getLivingShip(target.r, target.c);
            double damage = 0.0;
            for(Coordinate attacker : attackers)
            {
                damage += plan.computeDamage(attacker, si, multiplier);
            }
            // and apply the damage in one go
            si.setDamage( si.getDamage().value() + damage);     // this might be enough to kill the guy.
        }
    }

    private static Map<Coordinate, List<Coordinate>> invertAttackers(
            Map<Coordinate, Coordinate> attackCombo)
    {
        Map<Coordinate, List<Coordinate>> attackersPerDefender = new HashMap<Coordinate, List<Coordinate>>();
        for(Map.Entry<Coordinate, Coordinate> e : attackCombo.entrySet())
        {
            Coordinate attacker = e.getKey();
            Coordinate defender = e.getValue();
            List<Coordinate> list = attackersPerDefender.get(defender);
            if(list == null)
            {
                list = new LinkedList<Coordinate>();
                attackersPerDefender.put(defender, list);
            }
            list.add(attacker);
        }
        return attackersPerDefender;
    }
}
