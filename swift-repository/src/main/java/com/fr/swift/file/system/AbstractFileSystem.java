package com.fr.swift.file.system;

import com.fr.swift.file.conf.SwiftFileSystemConfig;
import com.fr.swift.file.exception.SwiftFileException;

import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/28
 */
public abstract class AbstractFileSystem implements SwiftFileSystem {
    protected SwiftFileSystemConfig config;
    private URI uri;

    public AbstractFileSystem(SwiftFileSystemConfig config, URI uri) {
        this.config = config;
        this.uri = uri;
    }

    @Override
    public SwiftFileSystem[] listFiles() throws SwiftFileException {
        if (isFile()) {
            throw new SwiftFileException(String.format("File path '%s' is not directory!", uri.getPath()));
        }
        return list();
    }

    protected abstract SwiftFileSystem[] list() throws SwiftFileException;

    @Override
    public SwiftFileSystem read() throws SwiftFileException {
        return read(uri);
    }

    @Override
    public boolean write(InputStream inputStream) throws SwiftFileException {
        return write(uri, inputStream);
    }

    @Override
    public boolean remove() throws SwiftFileException {
        return remove(uri);
    }

    @Override
    public boolean renameTo(URI dest) throws SwiftFileException {
        return renameTo(uri, dest);
    }

    @Override
    public boolean copy(URI dest) throws SwiftFileException {
        return copy(uri, dest);
    }

    @Override
    public URI getResourceURI() {
        return uri;
    }

    @Override
    public String getResourceName() {
        return null;
    }
}
