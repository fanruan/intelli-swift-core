package com.fr.swift.file.system.impl;

import com.fr.swift.file.conf.SwiftFileSystemConfig;
import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;

import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftFileSystemImpl extends AbstractFileSystem {
    public SwiftFileSystemImpl(SwiftFileSystemConfig config, URI uri) {
        super(config, uri);
    }

    @Override
    public void write(URI remote, InputStream inputStream) {
    }

    @Override
    public SwiftFileSystem read(URI remote) {
        return null;
    }

    @Override
    public boolean remove(URI remote) {
        return false;
    }

    @Override
    public boolean renameTo(URI src, URI dest) {
        return false;
    }

    @Override
    public boolean copy(URI src, URI dest) {
        return false;
    }

    @Override
    public boolean isExists() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public InputStream toStream() {
        return null;
    }

    @Override
    public String getResourceName() {
        return null;
    }

    @Override
    public void mkdirs() {

    }

    @Override
    public void close() {

    }

    @Override
    protected SwiftFileSystem[] list() {
        return new SwiftFileSystem[0];
    }
}
