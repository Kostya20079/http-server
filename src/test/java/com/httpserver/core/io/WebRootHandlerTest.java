package com.httpserver.core.io;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebRootHandlerTest {

    private WebRootHandler webRootHandler;

    private Method checkIfEndsWithSlashMethod;

    private Method checkIfProvidedRelativePathExistMethod;

    @BeforeAll
    public void beforeClass() throws WebRootNotFoundException, NoSuchMethodException {
        webRootHandler = new WebRootHandler("WebRoot");
        Class<WebRootHandler> cls = WebRootHandler.class;
        checkIfEndsWithSlashMethod = cls.getDeclaredMethod("checkIfEndsWithSlash", String.class);
        checkIfEndsWithSlashMethod.setAccessible(true);

        checkIfProvidedRelativePathExistMethod =  cls.getDeclaredMethod("checkIfProvidedRelativePathExist", String.class);
        checkIfProvidedRelativePathExistMethod.setAccessible(true);
    }

    @Test
    public void constructorGoodPath() {
        try {
            WebRootHandler webRootHandler = new WebRootHandler("/media/kost/08EC038CEC03736C/Computer_Science/My project/Java/http-server/WebRoot");
        } catch (WebRootNotFoundException e) {
            fail(e);
        }
    }

    @Test
    public void constructorBadPath() {
        try {
            WebRootHandler webRootHandler = new WebRootHandler("/media/kost/08EC038CEC03736C/Computer_Science/My project/Java/http-server/WebRoot2");
            fail();
        } catch (WebRootNotFoundException ignored) {}
    }

    @Test
    public void constructorGoodPath2() {
        try {
            WebRootHandler webRootHandler = new WebRootHandler("WebRoot");
        } catch (WebRootNotFoundException e) {
            fail(e);
        }
    }

    @Test
    public void constructorBadPath2() {
        try {
            WebRootHandler webRootHandler = new WebRootHandler("WebRoot2");
            fail();
        } catch (WebRootNotFoundException ignored) {}
    }

    @Test
    public void checkIfEndsWithSlashMethodPathFalse () {
        try {
            boolean result = (Boolean)checkIfEndsWithSlashMethod.invoke(webRootHandler, "index.html");
            assertFalse(result) ;
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    public void checkIfEndsWithSlashMethodPathFalse2 () {
        try {
            boolean result = (Boolean)checkIfEndsWithSlashMethod.invoke(webRootHandler, "/index.html");
            assertFalse(result) ;
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    public void checkIfEndsWithSlashMethodPathFalse3() {
        try {
            boolean result = (Boolean)checkIfEndsWithSlashMethod.invoke(webRootHandler, "/private/index.html");
            assertFalse(result) ;
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    public void checkIfEndsWithSlashMethodPathTrue() {
        try {
            boolean result = (Boolean)checkIfEndsWithSlashMethod.invoke(webRootHandler, "/");
            assertTrue(result); ;
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    public void checkIfEndsWithSlashMethodPathTrue2() {
        try {
            boolean result = (Boolean)checkIfEndsWithSlashMethod.invoke(webRootHandler, "/private/");
            assertTrue(result); ;
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    public void checkIfProvidedRelativePathExistTest() {
        try {
            boolean result = (Boolean) checkIfProvidedRelativePathExistMethod.invoke(webRootHandler, "/index.html");
            assertTrue(result);
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    public void checkIfProvidedRelativePathExistTest1() {
        try {
            boolean result = (Boolean) checkIfProvidedRelativePathExistMethod.invoke(webRootHandler, "/./././index.html");
            assertTrue(result);
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    public void checkIfProvidedRelativePathNotExistTest() {
        try {
            boolean result = (Boolean) checkIfProvidedRelativePathExistMethod.invoke(webRootHandler, "/indexx.html");
            assertFalse(result);
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    public void checkIfProvidedRelativePathInvalidTest() {
        try {
            boolean result = (Boolean) checkIfProvidedRelativePathExistMethod.invoke(webRootHandler, "/../");
            assertFalse(result);
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    public void fileMimeTypeTest() {
        try {
            String mimeType = webRootHandler.getFileMimeType("/");
            assertEquals("text/html", mimeType);
        } catch (FileNotFoundException e) {
            fail(e);
        }
    }

    @Test
    public void fileMimeTypeTestJpg() {
        try {
            String mimeType = webRootHandler.getFileMimeType("/img/example-image.jpg");
            assertEquals("image/jpeg", mimeType);
        } catch (FileNotFoundException e) {
            fail(e);
        }
    }

    @Test
    public void getFileByteArrayDataTest() {
        try {
            assertTrue(webRootHandler.getFileByteArrayData("/").length > 0);
        } catch (FileNotFoundException e) {
            fail(e);
        } catch (ReadFileException e) {
            fail(e);
        }
    }

    @Test
    public void getFileByteArrayDataFileNotThereTest() {
        try {
            webRootHandler.getFileByteArrayData("/test.html");
            fail();
        } catch (FileNotFoundException ignored) {
        } catch (ReadFileException e) {
            fail(e);
        }
    }
}