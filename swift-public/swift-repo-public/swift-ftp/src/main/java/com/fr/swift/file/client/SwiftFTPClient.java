package com.fr.swift.file.client;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2018-12-03
 */
public interface SwiftFTPClient {
    String[] listNames(String path) throws Exception;

    long getSize(String path) throws Exception;

    void write(String path, InputStream inputStream) throws Exception;

    boolean delete(String path) throws Exception;

    boolean rename(String src, String dest) throws Exception;

    boolean exists(String path) throws Exception;

    boolean isDirectory(String path) throws Exception;

    InputStream toStream(String path) throws Exception;

    boolean mkdirs(String path) throws Exception;

    void close() throws Exception;

    boolean login() throws Exception;

    void connect() throws Exception;

    boolean isConnected() throws IOException;

    void disconnect() throws IOException;
}
