package http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpHeadersParserTest {

    private HttpParser httpParser;
    private Method parseHeadersMethod;

    @BeforeAll
    public void beforeClass() throws NoSuchMethodException {
        httpParser = new HttpParser();
        Class<HttpParser> cls = HttpParser.class;
        parseHeadersMethod = cls.getDeclaredMethod("parseHeaders", InputStreamReader.class, HttpRequest.class);
        parseHeadersMethod.setAccessible(true);
    }

    @Test
    public void testSimpleSingleHeaderMessage() throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = new HttpRequest();
        parseHeadersMethod.invoke(
                httpParser,
                generateSimpleSingleHeaderMessage(),
                request
        );

        assertEquals(1, request.getHeaderNames().size() );
        assertEquals("localhost:8080", request.getHeader("host" ));
    }

    @Test
    public void testMultipleHeaderMessage() throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = new HttpRequest();
        parseHeadersMethod.invoke(
                httpParser,
                generateMultipleHeadersMessage(),
                request
        );

        assertEquals(11, request.getHeaderNames().size() );
        assertEquals("localhost:8080", request.getHeader("host" ));
    }

    @Test
    public void testSpaceBeforeColonErrorHeaderMessage() {
        HttpRequest request = new HttpRequest();
        try {
            parseHeadersMethod.invoke(
                    httpParser,
                    generateSpaceBeforeColonErrorHeaderMessage(),
                    request
            );
        } catch (InvocationTargetException | IllegalAccessException e) {
            if (e.getCause() instanceof HttpParsingException) {
                assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST,((HttpParsingException)e.getCause()).getStatusCode());
            }
        }
    }

    private InputStreamReader generateSimpleSingleHeaderMessage() {
        String rawData = "Host: localhost:8080\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(
                StandardCharsets.US_ASCII
        ));

        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        return reader;
    }

    private InputStreamReader generateMultipleHeadersMessage() {
        String rawData = "Host: localhost:8080\r\n" +
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

        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        return reader;
    }

    private InputStreamReader generateSpaceBeforeColonErrorHeaderMessage() {
        String rawData = "Host : localhost:8080\r\n\n";

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(
                StandardCharsets.US_ASCII
        ));

        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        return reader;
    }
}