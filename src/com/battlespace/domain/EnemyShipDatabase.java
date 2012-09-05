package com.battlespace.domain;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
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
        FileData fd = DataLoaderService.loadFileWithBackup("conf/enemy_ships.txt","data/enemy_ships.txt");
        Set<String> keys = fd.getKeys();
        for(String key : keys)
        {
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
            ps.code = key;
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

    public void update() throws IOException
    {
        if(dirty)
        {
            PrintStream w = new PrintStream(new FileOutputStream("conf/enemy_ships.txt"));
            
            // write the new version of the file
            for(EnemyShip ship : db.values())
            {
                String code = ship.getCode();
                String name = ship.getName();
                List<Stat> summary = ship.getSummaryStats();
                List<Stat> shield = ship.getShieldStats();
                Stat durability = ship.getDurability();
                Stat domination = ship.getDomination();
                Stat speed = ship.getSpeed();
                int units = ship.getUnits();
                
                StringBuffer sb = new StringBuffer(code + "=" + name + ",");
                sb.append(statsFormat(summary));
                sb.append(statsFormat(shield));
                sb.append(statFormat(durability));
                sb.append(statFormat(domination));
                sb.append(statFormat(speed));
                sb.append(Integer.toString(units));
                
                w.println(sb.toString());
            }
            w.close();
        }
    }

    private String statsFormat(List<Stat> summary)
    {
        StringBuffer sb = new StringBuffer();
        for(Stat stat : summary)
        {
            sb.append(statFormat(stat));
        }
        return sb.toString();
    }

    private String statFormat(Stat stat)
    {
        int min = (int)Math.floor(stat.value(false));
        int max = (int)Math.ceil(stat.value(true));
        if(min==max)
        {
            return Integer.toString(min)+",";
        }
        return Integer.toString(min)+"-"+Integer.toString(max)+",";
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

    public void refineDurability(EnemyShip nme, Stat refined)
    {
        Stat si = nme.getDurability();
        if(si instanceof RangedStat)
        {
            RangedStat ri = (RangedStat)si;
            if(refined.value(false) > ri.value(false))
            {
                ri.setMin(refined.value(false));
                dirty = true;
            }
            if(refined.value(true) < ri.value(true))
            {
                ri.setMax(refined.value(true));
                dirty = true;
            }
        }
    }

}
