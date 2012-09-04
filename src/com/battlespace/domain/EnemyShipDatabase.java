package com.battlespace.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.battlespace.service.DataLoaderService;
import com.battlespace.service.StatFactory;

public class EnemyShipDatabase
{
    Map<String, EnemyShip> db;
    boolean dirty = false;
    
    public static EnemyShipDatabase load() throws Exception
    {
        Map<String, EnemyShip> x = loadDatabase();
        return new EnemyShipDatabase(x);
    }
    
    private static Map<String, EnemyShip> loadDatabase() throws Exception
    {
        // looks a lot like the other one
        Map<String, EnemyShip> _psd = new HashMap<String, EnemyShip>();
        FileData fd = DataLoaderService.loadFile("data/enemy_ships.txt");
        Set<String> keys = fd.getKeys();
        for(String key : keys)
        {
            if(key.contains(".")) continue; // skip enhancements, labelled .0.1.2.3
            
            List<String> data = fd.getList(key);
            
            String name = data.get(0);
            Map<String, Stat> torp = AbsoluteStat.createWeaponStat(data.subList(1,4));
            Map<String, Stat> plas = AbsoluteStat.createWeaponStat(data.subList(4,7));
            Stat torpShield = StatFactory.create(data.get(7));
            Stat plasShield = StatFactory.create(data.get(8));
            Stat dur = StatFactory.create(data.get(9));
            Stat dom = StatFactory.create(data.get(10));
            Stat speed = StatFactory.create(data.get(11));
            int units = Integer.valueOf(data.get(12));
                            
            EnemyShip ps = new EnemyShip(name, torp, plas, torpShield, plasShield, dur, dom, speed, units);            
            _psd.put(name, ps);
        }
        return _psd;
    }
    
    public EnemyShipDatabase(Map<String, EnemyShip> x)
    {
        this.db = x;
    }

    public EnemyShipInstance instantiate(String name) throws Exception
    {
        EnemyShip nme = lookup(name);
        return nme.createInstance();
    }

    public void update()
    {
        // TODO Auto-generated method stub
        
    }

    public EnemyShip lookup(String name) throws Exception
    {
        EnemyShip nme = db.get(name);
        if(nme == null)
        {
            throw new Exception("Could not load enemy ship " + name);
        }
        return nme;
    }

    public void refineSummaryStats(EnemyShip nme,
            List<Stat> newStatEstimate) throws Exception
    {
        System.out.println(newStatEstimate);
        
        List<Stat> initial = nme.getSummaryStats();
        List<Stat> improved = RangedStat.merge(initial, newStatEstimate);
        
        for(int i=0;i<6;i++)
        {
            Stat si = initial.get(i);
            if(si instanceof RangedStat)
            {
                RangedStat ri = (RangedStat)si;
                Stat j = improved.get(i);
                if(j.value(false) > ri.value(false))
                {
                    ri.setMin(j.value(false));
                    dirty = true;
                }
                if(j.value(true) < ri.value(true))
                {
                    ri.setMax(j.value(true));
                    dirty = true;
                }
            }
        }
    }

}
