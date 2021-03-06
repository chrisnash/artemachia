package com.battlespace.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class Deployment
{
    SortedMap<Coordinate, ShipInstance> deploymentMap;
    int interceptionKills = 0;
    Integer cachedFrontLine;
    
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
    
    public double getEffectiveDomination()
    {
        double results = 0.0;
        for(ShipInstance instance : deploymentMap.values())
        {
            Ship ship = instance.getParent();
            double c = instance.getEffectiveCount();
            
            results += c * ship.getDomination().value();
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
        List<Coordinate> out = new LinkedList<Coordinate>();
        for(Map.Entry<Coordinate, ShipInstance> e : deploymentMap.entrySet())
        {
            Coordinate k = e.getKey();
            ShipInstance v = e.getValue();
            if(k.c > cachedFrontLine+1) continue;
            if(v.isAlive()) out.add(k);
        }
        return out;
    }

    public boolean isAlive()
    {
       for(ShipInstance v : deploymentMap.values())
       {
            if(v.isAlive()) return true;
       }
       return false;
    }
    
    public String toString()
    {
        return deploymentMap.toString();
    }

    public int shipsLost()
    {
        int l=0;
        for(ShipInstance v : deploymentMap.values())
        {
             if(!v.isAlive()) l++;
        }
        return l + interceptionKills;
    }

    public double damageRatio()
    {
        double dam = 0.0;
        for(ShipInstance v : deploymentMap.values())
        {
            double d = v.getDamage().value();
            double m = v.getParent().getDurability().value();
            if(d<0.0) d=0.0;
            if(d>m) d=m;
            dam += (d/m);
        }
        return dam + interceptionKills;
    }

    public int replacementTime()
    {
        Map<String,Integer> rtBySize = new HashMap<String,Integer>();
        
        for(ShipInstance v : deploymentMap.values() )
        {
            if(!v.isAlive())
            {
                int rt = v.getParent().getReplacementTime();
                String size = v.getParent().getSize();
                Integer oldRt = rtBySize.get(size);
                if(oldRt != null)
                {
                    rt += oldRt.intValue();
                }
                rtBySize.put(size, Integer.valueOf(rt));
            }
        }
        int rt = 0;
        for(Integer rts : rtBySize.values())
        {
            if(rts.intValue() > rt)
            {
                rt = rts.intValue();
            }
        }
        return rt;
    }
    
    public int buildTime()
    {
        Map<String,Integer> rtBySize = new HashMap<String,Integer>();
        
        for(ShipInstance v : deploymentMap.values() )
        {
                int rt = v.getParent().getReplacementTime();
                String size = v.getParent().getSize();
                Integer oldRt = rtBySize.get(size);
                if(oldRt != null)
                {
                    rt += oldRt.intValue();
                }
                rtBySize.put(size, Integer.valueOf(rt));
        }
        int rt = 0;
        for(Integer rts : rtBySize.values())
        {
            if(rts.intValue() > rt)
            {
                rt = rts.intValue();
            }
        }
        return rt;
    }

    public double dataValue()
    {
        Set<Ship> distinctShips = new HashSet<Ship>();
        for(ShipInstance v : deploymentMap.values())
        {
            distinctShips.add(v.getParent());
        }
        double dv = 0.0;
        for(Ship s : distinctShips)
        {
            dv += s.dataValue();
        }
        return dv;
    }

    public void reboot() throws Exception
    {
        for(ShipInstance v : deploymentMap.values())
        {
            v.reboot();
            interceptionKills++;
        }
    }

    public void beginTurn()
    {
        cachedFrontLine = frontLine();
    }
    
    public void endTurn()
    {
        cachedFrontLine = null;
    }

    public double heroism()
    {
        double h = 0.0;
        List<Coordinate> survivors = livingShipList();
        for(Coordinate survivor : survivors)
        {
            ShipInstance si = deploymentMap.get(survivor);
            double d = si.getDamage().value();
            double m = si.getParent().getDurability().value();
            double th = (100.0*d)/m;
            if(th>h) h=th;
        }
        return h;
    }
}
