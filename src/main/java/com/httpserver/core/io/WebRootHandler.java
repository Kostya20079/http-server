package com.httpserver.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

public class WebRootHandler  {
    private File webRoot;

    public WebRootHandler(String webRootPath) throws WebRootNotFoundException {
        webRoot = new File(webRootPath);
        if (!webRoot.exists() || !webRoot.isDirectory()) {
            throw new WebRootNotFoundException("Webroot provided does not exist or there is not any folder");
        }
    }

    private boolean checkIfEndsWithSlash(String relativePath) {
        return relativePath.endsWith("/");
    }

    private boolean checkIfProvidedRelativePathExist(String relativePath) {
        File file = new File(webRoot, relativePath);

        if (!file.exists()) {
            return false;
        }

        try {
            if (file.getCanonicalPath().startsWith(webRoot.getCanonicalPath())) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    public String getFileMimeType(String relativePath) throws FileNotFoundException {
        if (checkIfEndsWithSlash(relativePath)) {
            relativePath += "index.html"; // by default
        }

        if (!checkIfProvidedRelativePathExist(relativePath)) {
            throw new FileNotFoundException("File not found exception: " + relativePath);
        }

        File file = new File(webRoot, relativePath);

        String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());

        if (mimeType == null) {
            return "application/octet-stream";
        }

        return mimeType;
    }

    /**
     *  Returns a byte array of the content of a file.
     *
     * @param relativePath path to the file in WebRoot folder.
     * @return a byte array of the data.
     * @throws FileNotFoundException if the file can not be found.
     * @throws ReadFileException if the problem of reading file.
     * */
    public byte[] getFileByteArrayData(String relativePath) throws FileNotFoundException, ReadFileException {
        if (checkIfEndsWithSlash(relativePath)) {
            relativePath += "index.html"; // by default
        }

        if (!checkIfProvidedRelativePathExist(relativePath)) {
            throw new FileNotFoundException("File not found exception: " + relativePath);
        }

        File file = new File(webRoot, relativePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[(int)file.length()];
        try {
            fileInputStream.read(fileBytes);
            fileInputStream.close();
        } catch (IOException e) {
            throw new ReadFileException(e);
        }
        return fileBytes;
    }
}