package com.fr.swift.file.system;

import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.repository.utils.SwiftRepositoryUtils;
import com.fr.swift.util.Strings;

import java.io.InputStream;

/**
 * @author yee
 * @date 2018/5/28
 */
public abstract class AbstractFileSystem<Config extends SwiftFileSystemConfig> implements SwiftFileSystem {
    protected Config config;
    private String uri;

    public AbstractFileSystem(Config config, String uri) {
        this.config = config;
        this.uri = uri;
    }

    @Override
    public SwiftFileSystem[] listFiles() throws SwiftFileException {
        if (!isDirectory()) {
            throw new SwiftFileException(String.format("File path '%s' is not directory!", uri));
        }
        return list();
    }

    protected abstract SwiftFileSystem[] list() throws SwiftFileException;

    @Override
    public SwiftFileSystem read() throws SwiftFileException {
        return read(uri);
    }

    @Override
    public void write(InputStream inputStream) throws SwiftFileException {
        write(uri, inputStream);
    }

    @Override
    public boolean remove() throws SwiftFileException {
        return remove(uri);
    }

    @Override
    public boolean renameTo(String dest) throws SwiftFileException {
        return renameTo(uri, dest);
    }

    @Override
    public boolean copy(String dest) throws SwiftFileException {
        return copy(uri, dest);
    }

    @Override
    public String getResourceURI() {
        return uri;
    }

    protected String getParentURI() {
        return SwiftRepositoryUtils.getParent(uri);
    }

    @Override
    public Config getConfig() {
        return config;
    }

    protected String resolve(String uri, String resolve) {
        return Strings.unifySlash(uri + "/" + resolve);
    }

    @Override
    public long getSize() {
        try {
            long size = 0;
            SwiftFileSystem[] systems = this.listFiles();
            for (SwiftFileSystem system : systems) {
                size += system.getSize();
            }
            return size;
        } catch (SwiftFileException e) {
            return fileSize();
        }
    }

    protected abstract long fileSize();
}
