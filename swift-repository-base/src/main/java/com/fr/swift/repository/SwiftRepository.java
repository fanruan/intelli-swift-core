package com.fr.swift.repository;

import java.io.IOException;

/**
 * @author yee
 * @date 2018/5/28
 */
public interface SwiftRepository {
    String copyFromRemote(String remote, String local) throws IOException;

    boolean copyToRemote(String local, String remote) throws IOException;

    boolean zipToRemote(String local, String remote) throws IOException;

    boolean delete(String remote) throws IOException;

    long getSize(String path) throws IOException;

    boolean exists(String path);

    void testConnection() throws Exception;
}
