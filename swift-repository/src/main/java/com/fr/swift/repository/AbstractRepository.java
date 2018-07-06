package com.fr.swift.repository;

import com.fr.swift.file.conf.SwiftFileSystemConfig;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.impl.DefaultFileSystemImpl;
import com.fr.swift.file.system.pool.RemotePoolCreator;

import java.io.IOException;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/28
 */
public abstract class AbstractRepository implements SwiftRepository {

    protected SwiftFileSystemConfig configuration;

    public AbstractRepository(SwiftFileSystemConfig configuration) {
        this.configuration = configuration;
    }

    @Override
    public abstract URI copyFromRemote(URI remote, URI local) throws IOException;

    @Override
    public abstract boolean copyToRemote(URI local, URI remote) throws IOException;

    @Override
    public abstract boolean zipToRemote(URI local, URI remote) throws IOException;

    @Override
    public abstract boolean delete(URI remote) throws IOException;

    public SwiftFileSystem createFileSystem(URI uri) {
        switch (configuration.getType()) {
            case FR:
                return new DefaultFileSystemImpl(configuration, uri);
            default:
                return RemotePoolCreator.creator().getPool(configuration).borrowObject(uri);
        }
    }

    public void closeFileSystem(SwiftFileSystem fileSystem) throws SwiftFileException {
        if (null != fileSystem) {
            switch (configuration.getType()) {
                case FR:
                    fileSystem.close();
                default:
                    RemotePoolCreator.creator().getPool(configuration).returnObject(fileSystem.getResourceURI(), fileSystem);
            }
        }
    }
}
