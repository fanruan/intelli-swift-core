package com.fr.swift.repository;

import com.fr.swift.file.conf.SwiftFileSystemConfig;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.impl.DefaultFileSystemImpl;
import com.fr.swift.file.system.impl.SwiftFileSystemImpl;

import java.io.IOException;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/28
 */
public abstract class AbstractRepository implements SwiftRepository {

    protected SwiftFileSystemConfig configuration;
    private SwiftFileSystem fileSystem;

    public AbstractRepository(SwiftFileSystemConfig configuration) {
        this.configuration = configuration;
    }

    @Override
    public abstract URI copyFromRemote(URI remote, URI local) throws IOException;

    @Override
    public abstract boolean copyToRemote(URI local, URI remote) throws IOException;

    private SwiftFileSystem createFileSystem(URI uri) {
        switch (configuration.getType()) {

            case SWIFT:
                return new SwiftFileSystemImpl(configuration, uri);
            default:
                return new DefaultFileSystemImpl(configuration, uri);
        }
    }

    public SwiftFileSystem getFileSystem(URI uri) {
        if (null == fileSystem) {
            fileSystem = createFileSystem(uri);
        }
        return fileSystem;
    }
}
