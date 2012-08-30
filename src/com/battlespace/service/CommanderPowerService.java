package com.battlespace.service;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.FileData;

public class CommanderPowerService
{
    static Map<String, CommanderPower> _cps;
    
    public static CommanderPower get(String name) throws Exception
    {
        Map<String, CommanderPower> cps = loadDatabase();
        CommanderPower cp = cps.get(name);
        if(cp == null)
        {
            throw new Exception("Could not load commander power " + name);
        }
        return cp;
    }

    private static Map<String, CommanderPower> loadDatabase() throws Exception
    {
        if(_cps == null)
        {
            _cps = new HashMap<String, CommanderPower>();
            
            FileData fd = DataLoaderService.loadFile("data/commander_bonuses.txt");
            Set<String> keys = fd.getKeys();
            for(String key : keys)
            {
                List<String> data = fd.getList(key);
                String name = data.get(0);
                String className = data.get(1);
                List<String> params = data.subList(2, data.size());
                
                Class<? extends CommanderPower> clazz = (Class<? extends CommanderPower>) Class.forName("com.battlespace.domain.commander." + className);
                Constructor<? extends CommanderPower> constructor = clazz.getConstructor(List.class);
                CommanderPower cp = constructor.newInstance(params);
                _cps.put(name, cp);
            }
        }
        return _cps;
    }

}
