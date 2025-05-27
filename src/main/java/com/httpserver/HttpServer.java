package com.httpserver;

import com.httpserver.config.Configuration;
import com.httpserver.config.ConfigurationManager;
import com.httpserver.core.ServerListenerThread;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Driver Class for the Http Server
 *
 **/

public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) {

        LOGGER.info("Server starting...") ;

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration config = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Reading at port: " + config.getPort());
        LOGGER.info("Using WebRoot: " + config.getWebroot());


        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(config.getPort(), config.getWebroot());
            serverListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}