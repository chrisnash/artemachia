package com.battlespace.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.battlespace.domain.FileData;
import com.battlespace.domain.Formation;
import com.battlespace.domain.PlanetData;

public class PlanetService
{
    static Map<String, PlanetData> _pd;
    
    public static Map<String, PlanetData> loadDatabase() throws Exception
    {
        if(_pd==null)
        {
            _pd = new HashMap<String,PlanetData>();
            FileData f = DataLoaderService.loadFile("data/planet_types.txt");
            Set<String> keys = f.getKeys();
            for(String key : keys)
            {
                List<String> value = f.getList(key);
                
                String code = key;
                int level = Integer.valueOf(value.get(0));
                String planets = PlanetData.sanitize(value.get(1));
                String formation = value.get(2);
                List<String> enemies = value.subList(3, value.size());
             
                PlanetData p = new PlanetData(code, level, planets, formation, enemies);
                _pd.put(key, p);
            }
        }
        return _pd;
    }

    public static PlanetData lookup(String string) throws Exception
    {
        Map<String, PlanetData> pd = loadDatabase();
        PlanetData p = pd.get(string);
        if(p==null) throw new Exception("Cannot find planet " + string);
        return p;
    }

    public static PlanetData lookupByLayout(int level, String conf) throws Exception
    {
        String layout = PlanetData.sanitize(conf);
        Map<String, PlanetData> pd = loadDatabase();
        for(PlanetData p : pd.values())
        {
            if((p.level==level)&&(p.planetConfig.equals(layout)))
            {
                return p;
            }
        }
        throw new Exception("Cannot find planet " + level + "," + conf);
    }
}
