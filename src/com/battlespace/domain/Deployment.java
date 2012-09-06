package com.battlespace.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class Deployment
{
    SortedMap<Coordinate, ShipInstance> deploymentMap;
    
    public Deployment(SortedMap<Coordinate, ShipInstance> deployData)
    {
        this.deploymentMap = deployData;
    }

    public List<Stat> getStats() throws Exception
    {
        List<Stat> stats = RangedStat.createEmptyStats();
        
        for(ShipInstance instance : deploymentMap.values())
        {
            List<Stat> shipStat = instance.getEffectiveStats();
            stats = RangedStat.sum(stats, shipStat);
        }
        return stats;
    }

    public Map<String, Double> getSummary()
    {
        Map<String, Double> results = new HashMap<String, Double>();
        for(ShipInstance instance : deploymentMap.values())
        {
            String name = instance.getParent().getName();
            double c = instance.getEffectiveCount();
            if(c > 0.0)
            {
                Double d = results.get(name);
                if(d!=null)
                {
                    c += d.doubleValue();
                }
                results.put(name, Double.valueOf(c));
            }
        }
        return results;
    }

    public Integer frontLine()
    {
        Integer fl = null;
        // the front row is actually the COLUMN of the frontmost living ship
        for(Map.Entry<Coordinate, ShipInstance> e : deploymentMap.entrySet())
        {
            Coordinate k = e.getKey();
            ShipInstance v = e.getValue();
            if(!v.isAlive()) continue;
            if((fl==null) || (k.c < fl))
            {
                fl = k.c;
            }
        }
        return fl;
    }

    public ShipInstance getLivingShip(int r, int c)
    {
        Coordinate k = new Coordinate(r, c);
        ShipInstance ship = deploymentMap.get(k);
        if( (ship != null) && (!ship.isAlive()) )
        {
            ship = null;
        }
        return ship;
    }

    public void updateDamage(int r, int c, DamageEntry damageEntry) throws Exception
    {
        if(damageEntry==null) return;
        Coordinate k = new Coordinate(r, c);
        ShipInstance ship = deploymentMap.get(k);
        if(ship==null) throw new Exception("No ship at " + k + " to apply damage");
        ship.updateDamage(damageEntry);
    }

    public Collection<ShipInstance> getAllShips()
    {
        return deploymentMap.values();
    }

    // everything down here was a mess to handle replays
    public Collection< List<Coordinate>> equivalenceClasses()
    {
        Map<String, List<Coordinate>> eqClasses = new HashMap<String, List<Coordinate>>();
        // two deployed living ships are equivalent if they have
        // same parent
        // matching stats
        for(Map.Entry<Coordinate, ShipInstance> e : deploymentMap.entrySet())
        {
            Coordinate k = e.getKey();
            ShipInstance v = e.getValue();
            
            // two ships are equivalent and interchangeable if everything after the first ( matches
            // you can even do better than that
            String shipKey = v.toString();
            int start = shipKey.indexOf("(");
            shipKey = shipKey.substring(start);
            
            List<Coordinate> l = eqClasses.get(shipKey);
            if(l==null)
            {
                l = new LinkedList<Coordinate>();
                eqClasses.put(shipKey,  l);
            }
            l.add(k);
        }
        //System.out.println(eqClasses);
        return eqClasses.values();
        // how to make this useful, is to determine if two attack vectors are equal up to equivalence classes.
        
    }

    public List<Map<Coordinate, Coordinate>> permutations()
    {
        Collection< List<Coordinate>> eqClasses = equivalenceClasses();
        //System.out.println(eqClasses);
        List<Map <Coordinate,Coordinate> > perms = new LinkedList<Map <Coordinate,Coordinate> >();
        perms.add( new HashMap<Coordinate,Coordinate>() );
        for(List<Coordinate> eq : eqClasses)
        {
            List< Map<Coordinate, Coordinate> > p = listToPerms(eq);
            perms = permProduct(perms, p);
        }
        //System.out.println(perms);
        return perms;
    }

    private List<Map<Coordinate, Coordinate>> permProduct(
            List<Map<Coordinate, Coordinate>> perms,
            List<Map<Coordinate, Coordinate>> p)
    {
        //System.out.println("PP: " + perms);
        //System.out.println("PP: " + p);
        List<Map <Coordinate,Coordinate>> out = new LinkedList<Map<Coordinate,Coordinate>>();
        for(Map<Coordinate,Coordinate> p1 : perms)
        {
            for(Map<Coordinate,Coordinate> p2 : p)
            {
                Map<Coordinate,Coordinate> m = new HashMap<Coordinate,Coordinate>();
                m.putAll(p1);
                m.putAll(p2);
                out.add(m);
            }
        }
        //System.out.println("PP: " + out);
        return out;
    }

    private List<Map<Coordinate, Coordinate>> listToPerms(List<Coordinate> eq)
    {
        //System.out.println("L2P: "+eq);
        // create all perms of the items in eq
        List< List<Coordinate> > pl = permCreator(eq);
        List< Map<Coordinate,Coordinate> > out = new LinkedList< Map<Coordinate,Coordinate> >();
        for(List<Coordinate> p : pl)
        {
            Map<Coordinate, Coordinate> m = mappingCreator(eq, p);
            out.add(m);
        }
        //System.out.println("L2P: "+out);
        return out;
    }

    private List<List<Coordinate>> permCreator(List<Coordinate> eq)
    {
        // create all permutations of the given list
        List<List<Coordinate>> out = new LinkedList<List<Coordinate>>();
        if(eq.size()==0)
        {
            // empty list, return one empty perm
            out.add(new LinkedList<Coordinate>());
        }
        else
        {
            // select each item, remove it, perm that, add the item
            for(int i=0;i<eq.size();i++)
            {
                Coordinate item = eq.get(i);
                List<Coordinate> x = new LinkedList<Coordinate>();
                x.addAll( eq.subList(0, i));
                x.addAll( eq.subList(i+1, eq.size()));
                List<List<Coordinate>> subPerm = permCreator(x);
                for(List<Coordinate> pp : subPerm)
                {
                    pp.add(item);
                    out.add(pp);
                }
            }
        }
        return out;
    }

    private Map<Coordinate, Coordinate> mappingCreator(List<Coordinate> domain,
            List<Coordinate> range)
    {
        Map<Coordinate, Coordinate> out = new HashMap<Coordinate, Coordinate>();
        if(domain.size() != range.size()) throw new UnsupportedOperationException();
        for(int i=0;i<domain.size();i++)
        {
            out.put(domain.get(i), range.get(i));
        }
        return out;
    }

    public List<Coordinate> livingShipList()
    {
        List<Coordinate> out = new LinkedList<Coordinate>();
        for(Map.Entry<Coordinate, ShipInstance> e : deploymentMap.entrySet())
        {
            Coordinate k = e.getKey();
            ShipInstance v = e.getValue();
            if(v.isAlive()) out.add(k);
        }
        return out;
    }
    public List<Coordinate> vulnerableShipList()
    {
        int fl = frontLine();
        List<Coordinate> out = new LinkedList<Coordinate>();
        for(Map.Entry<Coordinate, ShipInstance> e : deploymentMap.entrySet())
        {
            Coordinate k = e.getKey();
            ShipInstance v = e.getValue();
            if(k.c > fl+1) continue;
            if(v.isAlive()) out.add(k);
        }
        return out;
    }
}
