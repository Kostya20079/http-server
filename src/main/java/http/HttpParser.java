package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HttpParser  {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    private static final int SP = 0x20; // 32
    private static final int CR = 0x0D; // 13
    private static final int LF = 0x0A; // 10


    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII );

        HttpRequest request = new HttpRequest();

        try {
            parseRequestLine(reader, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            parseHeaders(reader, request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request;
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException {
        StringBuilder processingDataBuffer = new StringBuilder();

        boolean methodParsed = false;
        boolean requestTargetParsed = false;

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    LOGGER.debug("Request Line VERSION to process {}", processingDataBuffer);
                    if (!methodParsed || !requestTargetParsed) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    try {
                        request.setHttpVersion(processingDataBuffer.toString());
                    } catch (BadHttpVersionException e) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    return;
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }
            
            if (_byte == SP) {
                if (!methodParsed) {
                    LOGGER.debug("Request Line METHOD to process {}", processingDataBuffer);
                    request.setMethod(processingDataBuffer.toString());
                    methodParsed = true;
                } else if (!requestTargetParsed) {
                    LOGGER.debug("Request Line REQUEST TARGET to process {}", processingDataBuffer);
                    request.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed = true;
                } else {
                     throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST) ;
                }
                processingDataBuffer.delete(0, processingDataBuffer.length());
            } else {
                processingDataBuffer.append((char)_byte);
                if (!methodParsed) {
                     if (processingDataBuffer.length() > HttpMethod.MAX_LENGTH ) {
                        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                     }
                }
            }
        }
    }

    private void parseHeaders(InputStreamReader reader, HttpRequest request) throws IOException {
        StringBuilder processingDataBuffer = new StringBuilder();
        boolean crlFound = false;

        int _byte;
        while((_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    if(!crlFound) {
                        crlFound = true;

                        processSingleHeaderDataField(processingDataBuffer, request);
                        // clear buffer
                        processingDataBuffer.delete(0, processingDataBuffer.length());
                    } else {
                        return;
                    }
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } else {
                crlFound = false;
                processingDataBuffer.append((char)_byte);
            }
        }
    }

    private void processSingleHeaderDataField(StringBuilder processingDataBuffer, HttpRequest request) {
        String rowHeaderField = processingDataBuffer.toString();

        Pattern pattern = Pattern.compile("^(?<fieldName>[!#$%&'*+\\-./^_`|~\\dA-Za-z]+):\\s*(?<fieldValue>.*?)\\s*$");

        Matcher matcher = pattern.matcher(rowHeaderField);
        if (matcher.matches()) {
            // find a propper header
            String fieldName = matcher.group("fieldName");
            String fieldValue = matcher.group("fieldValue");
            request.addHeader(fieldName, fieldValue );
        } else  {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST );
        }
    }
}