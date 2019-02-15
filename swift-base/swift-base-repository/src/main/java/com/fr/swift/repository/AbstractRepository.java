package com.fr.swift.repository;

import com.fr.swift.file.SwiftFileSystemType;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.factory.SwiftFileSystemFactory;
import com.fr.swift.file.system.pool.RemoteFileSystemFactoryCreator;
import com.fr.swift.util.Strings;

/**
 * @author yee
 * @date 2018/5/28
 */
public abstract class AbstractRepository implements SwiftRepository {

    protected SwiftFileSystemConfig configuration;

    public AbstractRepository(SwiftFileSystemConfig configuration) {
        this.configuration = configuration;
    }

    public SwiftFileSystem createFileSystem(String uri) {
        return getFileSystemFactory().createFileSystem(configuration, uri);
    }

    protected void closeFileSystem(SwiftFileSystem fileSystem) throws SwiftFileException {
        if (null != fileSystem) {
            getFileSystemFactory().closeFileSystem(fileSystem);
        }
    }

    private SwiftFileSystemFactory getFileSystemFactory() {
        SwiftFileSystemFactory factory = null;
        RemoteFileSystemFactoryCreator creator = RemoteFileSystemFactoryCreator.creator();
        if (null == configuration) {
            factory = creator.getFactory("DEFAULT");
        } else {
            SwiftFileSystemType type = configuration.getType();
            factory = creator.getFactory(type.name());
        }
        return factory;
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
