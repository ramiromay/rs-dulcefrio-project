package com.realssoft.dulcefrio.api.config;

import lombok.Getter;
import lombok.Setter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

@Getter
@Setter
public class PropertiesConfiguration extends Properties
{

    private static final PropertiesConfiguration INSTANCE = new PropertiesConfiguration();
    private String userId;
    private String token;

    private PropertiesConfiguration()
    {
        super();
        load();
    }


    public static PropertiesConfiguration getInstance()
    {
        return INSTANCE;
    }

    public void load()
    {
        try
        {
            super.load(new FileReader("application.properties"));
            userId = String.valueOf(super.getProperty("user.id"));
            token = String.valueOf(super.getProperty("token"));

            super.store(new FileWriter("application.properties"),
                    "Copyright (C) 2022 RealsSoft.");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void close()
    {
        try
        {
            super.load(new FileReader("application.properties"));
            super.setProperty("user.id", userId);
            super.setProperty("token", token);
            super.store(new FileWriter("application.properties"),
                    "Copyright (C) 2023 RealsSoft.");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }





}
