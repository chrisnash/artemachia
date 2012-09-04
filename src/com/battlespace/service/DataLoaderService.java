package com.battlespace.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import com.battlespace.domain.FileData;

public class DataLoaderService
{

    public static FileData loadFile(String replayFile) throws IOException
    {
        Properties p = new Properties();
        InputStream is = new FileInputStream(replayFile);
        p.load(is);
        is.close();
        
        FileData fd = new FileData();
        
        Set<String> keys = p.stringPropertyNames();
        for(String key : keys)
        {
            String value = p.getProperty(key);
            String[] values = value.split(",");
            for(int i=0;i<values.length;i++)
            {
                values[i] = values[i].trim();
            }
            fd.add(key, values);
        }
        return fd;
    }
    
    public static FileData loadFileWithBackup(String primaryFile, String secondaryFile) throws IOException
    {
        try
        {
            return loadFile(primaryFile);
        }
        catch(final IOException e)
        {
            return loadFile(secondaryFile);
        }
    }

}
