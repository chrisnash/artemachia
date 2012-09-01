package com.battlespace.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.battlespace.domain.FileData;
import com.battlespace.domain.Formation;

public class FormationService
{
    static Map<String, Formation> _fd;
    
    public static Formation get(String name) throws Exception
    {
       Map<String, Formation> fd = loadDatabase();
       Formation f = fd.get(name);
       if(f==null)
       {
           throw new Exception("Could not load formation " + name);
       }
       return f;
    }
    
    public static Map<String, Formation> loadDatabase() throws Exception
    {
        if(_fd==null)
        {
            _fd = new HashMap<String,Formation>();
            FileData f = DataLoaderService.loadFile("data/formations.txt");
            Set<String> keys = f.getKeys();
            for(String key : keys)
            {
                String value = f.get(key);
                // the value should be 35 long, all .'s and X's.
                Formation form = new Formation(value);
                _fd.put(key, form);
            }
        }
        return _fd;
    }

}
