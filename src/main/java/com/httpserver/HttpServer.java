package com.httpserver;

import com.httpserver.config.Configuration;
import com.httpserver.config.ConfigurationManager;

/**
 *
 * Driver Class for the Http Server
 *
 **/

public class HttpServer {
    public static void main(String[] args) {

        System.out.println("Server starting...") ;

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration config = ConfigurationManager.getInstance().getCurrentConfiguration();

        System.out.println("Reading at port: " + config.getPort());
        System.out.println("Using WebRoot: " + config.getWebroot());
    }
}