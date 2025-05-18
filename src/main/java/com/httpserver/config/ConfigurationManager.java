package com.httpserver.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.httpserver.util.JSON;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigurationManager {

    public static ConfigurationManager myConfigurationManager;
    public static Configuration myCurrentConfiguration; 


    private ConfigurationManager() {}

    public static ConfigurationManager getInstance() {
        if(myConfigurationManager == null) {
            myConfigurationManager = new ConfigurationManager();
        }
        return myConfigurationManager;
    }

    /**
     * Used to load a configuration file by the path provided
     **/
    public void loadConfigurationFile(String filePath) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new HttpConfigurationException(e);
        }

        StringBuffer sb = new StringBuffer();

        int i;
        try {
            while((i = fileReader.read()) != -1) {
                sb.append((char)i);
            }
        } catch (IOException e) {
            throw new HttpConfigurationException(e);
        }

        JsonNode config;
        try {
            config = JSON.parse(sb.toString());
        } catch (IOException e) {
            throw new HttpConfigurationException("Error parsing the configuration file", e);
        }

        try {
            myCurrentConfiguration = JSON.fromJson(config, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error parsing the configuration file, internal", e);
        }
    }

    /**
     *  Returns current loaded configuration
     **/
    public Configuration getCurrentConfiguration() {
        if(myCurrentConfiguration == null) {
            throw new HttpConfigurationException("No Current Configuration found.");
        }
        return myCurrentConfiguration;
    }
}
