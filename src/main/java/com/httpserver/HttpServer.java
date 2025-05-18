package com.httpserver;

import com.httpserver.config.Configuration;
import com.httpserver.config.ConfigurationManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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

        try {
            ServerSocket serverSocket = new ServerSocket(config.getPort());
            Socket socket = serverSocket.accept();

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            String html = "<html>" +
                    "<head><title>Java HTTP Server</title></head>" +
                    "<body><h1>This page was served using Java HTTP Server</h1></body>" +
                    "</html>";

            final String CRLF = "\r\n";

            String response =
                    "HTTP/1.1 200 OK" + CRLF + // Status line : HTTP VERSION RESPONSE_CODE RESPONSE_MESSAGE
                    "Content-length: " + html.getBytes().length + CRLF + // HEADER
                        CRLF +
                        html +
                        CRLF + CRLF;

            outputStream.write(response.getBytes());

            inputStream.close();
            outputStream.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}