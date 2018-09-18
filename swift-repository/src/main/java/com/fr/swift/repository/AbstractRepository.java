package com.fr.swift.repository;

import com.fr.swift.file.SwiftFileSystemType;
import com.fr.swift.file.SwiftRemoteFileSystemType;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.impl.DefaultFileSystemImpl;
import com.fr.swift.file.system.pool.RemoteFactoryCreator;
import com.fr.swift.repository.config.DefaultRepositoryConfig;
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
        if (null == configuration) {
            configuration = DefaultRepositoryConfig.INSTANCE;
        }
        SwiftFileSystemType type = configuration.getType();
        if (SwiftRemoteFileSystemType.FR.equals(type)) {
            return new DefaultFileSystemImpl(configuration, uri);
        } else {
            return RemoteFactoryCreator.creator().getFactory(type).createFileSystem(configuration, uri);
        }
    }

    public void closeFileSystem(SwiftFileSystem fileSystem) throws SwiftFileException {
        if (null != fileSystem) {
            SwiftFileSystemType type = configuration.getType();
            if (SwiftRemoteFileSystemType.FR.equals(type)) {
                fileSystem.close();
            } else {
                RemoteFactoryCreator.creator().getFactory(type).closeFileSystem(fileSystem);
            }
        }
    }

    protected String resolve(String uri, String resolve) {
        return Strings.unifySlash(uri + "/" + resolve);
    }

    @Override
    public boolean exists(String path) {
        SwiftFileSystem fileSystem = createFileSystem(path);
        try {
            return fileSystem.isExists();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                closeFileSystem(fileSystem);
            } catch (SwiftFileException ignore) {
            }
        }
    }

    @Override
    public void testConnection() throws Exception {
        SwiftFileSystem fileSystem = createFileSystem("");
        try {
            fileSystem.testConnection();
        } finally {
            closeFileSystem(fileSystem);
        }
    }
}
