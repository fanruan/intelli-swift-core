package com.fr.swift.repository.impl;

import com.fr.swift.repository.SwiftRepository;

import java.io.IOException;

/**
 * @author yee
 * @date 2019-08-07
 */
public class MutableRepository implements SwiftRepository {
    @Override
    public String copyFromRemote(String remote, String local) throws IOException {
        return null;
    }

    @Override
    public boolean copyToRemote(String local, String remote) throws IOException {
        return false;
    }

    @Override
    public boolean zipToRemote(String local, String remote) throws IOException {
        return false;
    }

    @Override
    public boolean delete(String remote) throws IOException {
        return false;
    }

    @Override
    public long getSize(String path) throws IOException {
        return 0;
    }

    @Override
    public boolean exists(String path) {
        return false;
    }
}
