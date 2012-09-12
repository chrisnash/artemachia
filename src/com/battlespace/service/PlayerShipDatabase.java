package com.battlespace.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.battlespace.domain.AbsoluteStat;
import com.battlespace.domain.FileData;
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.PlayerShipInstance;
import com.battlespace.domain.ShipEnhancement;
import com.battlespace.domain.Stat;

public class PlayerShipDatabase
{
    static Map<String, PlayerShip> _psd = null;
    
    public static PlayerShipInstance instantiate(String name) throws Exception
    {
        PlayerShip ps = lookup(name);
        return ps.createInstance();
    }

    public static PlayerShip lookup(String name) throws Exception
    {
        Map<String, PlayerShip> psd = loadDatabase();
        PlayerShip ps = psd.get(name);
        if(ps == null)
        {
            throw new Exception(name + " was not found in the database");
        }
        return ps;
    }
    
    private static Map<String, PlayerShip> loadDatabase() throws Exception
    {
        if(_psd == null)
        {
            _psd = new HashMap<String, PlayerShip>();
            FileData fd = DataLoaderService.loadFile("data/player_ships.txt");
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
                int buildTime = Integer.valueOf(data.get(13));
                                
                PlayerShip ps = new PlayerShip(name, torp, plas, torpShield, plasShield, dur, dom, speed, units, buildTime);
                
                for(int i=0; i<4; i++)
                {
                    String subkey = key + "." + i;
                    
                    List<String> _data = fd.getList(subkey);
                    
                    if(_data!=null)
                    {
                        String _name = _data.get(0);
                        Map<String, Stat> _torp = AbsoluteStat.createWeaponStat(_data.subList(1,4));
                        Map<String, Stat> _plas = AbsoluteStat.createWeaponStat(_data.subList(4,7));
                        Stat _torpShield = StatFactory.create(_data.get(7));
                        Stat _plasShield = StatFactory.create(_data.get(8));
                        Stat _dur = StatFactory.create(_data.get(9));
                        Stat _dom = StatFactory.create(_data.get(10));
                        Stat _speed = StatFactory.create(_data.get(11));
                        
                        ShipEnhancement se = new ShipEnhancement(name, _torp, _plas, _torpShield, _plasShield, _dur, _dom, _speed, units);
                        ps.registerEnhancement(i,se);
                    }
                    else
                    {
                        ps.registerEnhancement(i, null);    // temporary, until player ship db contains upgrades
                    }
                }
                
                ps.code = key;
                _psd.put(name, ps);
                _psd.put(key, ps);  // works for codes too
            }
        }
        return _psd;
    }
}
