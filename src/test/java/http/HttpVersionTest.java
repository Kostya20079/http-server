package http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HttpVersionTest {

    @Test
    void getBestCompatibleVersionExactMatch() {
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleHttpVersion("HTTP/1.1");
        } catch (BadHttpVersionException e) {
            fail();
        }

        assertNotNull(version);
        assertEquals(HttpVersion.HTTP_1_1, version);
    }

    @Test
    void getBestCompatibleVersionBadFormat() {
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleHttpVersion("http/1.1");
            fail();
        } catch (BadHttpVersionException ignored) {

        }
     }

    @Test
    void getBestCompatibleVersionHigherVersion() {
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleHttpVersion("HTTP/1.2");
            assertNotNull(version);
            assertEquals(HttpVersion.HTTP_1_1, version);
        } catch (BadHttpVersionException ignored) {
            fail();
        }
    }

}