package com.httpserver.core;

import com.httpserver.core.io.ReadFileException;
import com.httpserver.core.io.WebRootHandler;
import com.httpserver.core.io.WebRootNotFoundException;
import http.HttpParser;
import http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpConnectionWorkerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    final String CRLF = "\r\n";

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            HttpParser parser = new HttpParser();
            HttpRequest request = parser.parseHttpRequest(inputStream);

            String relativePath = request.getRequestTarget();

            WebRootHandler webRootHandler = null;
            try {
                webRootHandler = new WebRootHandler("WebRoot");
            } catch (WebRootNotFoundException e) {
                throw new RuntimeException(e);
            }

            try {
                byte[] fileData = webRootHandler.getFileByteArrayData(relativePath);
                String contentType = webRootHandler.getFileMimeType(relativePath);

                String response = "HTTP/1.1 200 OK" + CRLF +
                        "Content-Type: " + contentType + CRLF +
                        "Content-Length: " + fileData.length + CRLF +
                        CRLF;

                outputStream.write(response.getBytes());
                outputStream.write(fileData);
            } catch (FileNotFoundException e) {
                String notFoundHtml = "<html><body><h1>404 - Not Found</h1></body></html>";
                String responseHeader = "HTTP/1.1 404 Not Found" + CRLF +
                        "Content-Type: text/html" + CRLF +
                        "Content-Length: " + notFoundHtml.getBytes().length + CRLF +
                        CRLF;
                outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
                outputStream.write(notFoundHtml.getBytes(StandardCharsets.UTF_8));

            } catch (ReadFileException e) {
                LOGGER.error("Error reading file", e);
            }
            LOGGER.info(" *  Connection Processing Finished.");
        } catch (IOException e) {
            LOGGER.error("Problem with communication", e);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {}
            }

            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {}
            }

            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
