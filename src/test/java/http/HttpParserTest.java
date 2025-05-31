package http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpParserTest {

    private HttpParser httpParser;

    @BeforeAll
    public void beforeClass() {
        httpParser = new HttpParser();
    }

    @Test
    void parseHttpRequest() {
        HttpRequest request = null;
        try {
            request = httpParser.parseHttpRequest(
                    generateValidGETestCase()
            );
        } catch (HttpParsingException e) {
            fail(e);
        }

        assertEquals(HttpMethod.GET, request.getMethod());
    }

    @Test
    void parseHttpRequestBadMethod1() {
        try {
            HttpRequest request =  httpParser. parseHttpRequest(
                    generateBadTestCaseMethodName1()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, e.getStatusCode());
        }
    }

    @Test
    void parseHttpRequestBadMethod2() {
        try {
            HttpRequest request =  httpParser. parseHttpRequest(
                    generateBadTestCaseMethodName2()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, e.getStatusCode());
        }
    }

    @Test
    void parseHttpRequestBadInvNumItems1() {
        try {
            HttpRequest request =  httpParser. parseHttpRequest(
                    generateBadTestCaseRequestLineInvNumItems1()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    void parseHttpEmptyRequestLine() {
        try {
            HttpRequest request =  httpParser. parseHttpRequest(
                    generateBadTestCaseEmptyRequestLine()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    void parseHttpEmptyRequestLineOnlyCRnoLF() {
        try {
            HttpRequest request =  httpParser. parseHttpRequest(
                    generateBadTestCaseEmptyRequestLineOnlyCRnoLF()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getStatusCode());
        }
    }

    private InputStream generateValidGETestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:139.0) Gecko/20100101 Firefox/139.0\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" +
                "Accept-Language: en-US,en;q=0.5\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Connection: keep-alive\r\n" +
                "Cookie: Webstorm-b85e4ebb=<REDACTED>\\r\\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Priority: u=0, i";

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(
                StandardCharsets.US_ASCII
        ));

        return inputStream;
    }

    private InputStream generateBadTestCaseMethodName1() {
        String rawData = "GeT / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(
                StandardCharsets.US_ASCII
        ));

        return inputStream;
    }

    private InputStream generateBadTestCaseMethodName2() {
        String rawData = "GETTT / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(
                StandardCharsets.US_ASCII
        ));

        return inputStream;
    }

    private InputStream generateBadTestCaseRequestLineInvNumItems1() {
        String rawData = "GET / AAA HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(
                StandardCharsets.US_ASCII
        ));

        return inputStream;
    }

    private InputStream generateBadTestCaseEmptyRequestLine() {
        String rawData = "\r\n" +
                "Host: localhost:8080\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(
                StandardCharsets.US_ASCII
        ));

        return inputStream;
    }

    private InputStream generateBadTestCaseEmptyRequestLineOnlyCRnoLF() {
        String rawData = "GET /  HTTP/1.1\r" + // <---- no LF
                "Host: localhost:8080\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(
                StandardCharsets.US_ASCII
        ));

        return inputStream;
    }
}