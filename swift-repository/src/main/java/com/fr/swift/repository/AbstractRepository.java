package com.fr.swift.repository;

import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.impl.DefaultFileSystemImpl;
import com.fr.swift.file.system.pool.RemotePoolCreator;
import com.fr.swift.util.Strings;

import java.io.IOException;

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
    public abstract String copyFromRemote(String remote, String local) throws IOException;

    @Override
    public abstract boolean copyToRemote(String local, String remote) throws IOException;

    @Override
    public abstract boolean zipToRemote(String local, String remote) throws IOException;

    @Override
    public abstract boolean delete(String remote) throws IOException;

    public SwiftFileSystem createFileSystem(String uri) {
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
                    break;
                default:
                    RemotePoolCreator.creator().getPool(configuration).returnObject(fileSystem.getResourceURI(), fileSystem);
            }
        }
    }

    protected String resolve(String uri, String resolve) {
        return Strings.trimSeparator(uri + "/" + resolve, "/");
    }
}
