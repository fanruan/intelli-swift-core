package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.annotation.FileSystemFactory;
import com.fr.swift.file.system.factory.BasePooledFileSystemFactory;
import com.fr.swift.repository.SwiftFileSystemConfig;

import java.util.Properties;

/**
 * @author yee
 * @date 2019-01-08
 */
@FileSystemFactory(name = "TEST")
public class TestRemoteSystemFactory extends BasePooledFileSystemFactory {
    @Override
    protected RemoteFileSystemPool createPool(SwiftFileSystemConfig config) {
        return new TestRemoteSystemPool(config);
    }

    @Override
    public Object loadFromProperties(Properties properties) {
        return null;
    }
}
