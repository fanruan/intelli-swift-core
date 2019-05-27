package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.annotation.PackageConnectorFactory;
import com.fr.swift.file.system.factory.BasePooledFileSystemFactory;
import com.fr.swift.repository.PackageConnectorConfig;

import java.util.Properties;

/**
 * @author yee
 * @date 2019-01-08
 */
@PackageConnectorFactory(name = "TEST")
public class TestRemoteSystemFactory extends BasePooledFileSystemFactory {
    @Override
    protected RemoteFileSystemPool createPool(PackageConnectorConfig config) {
        return new TestRemoteSystemPool(config);
    }

    @Override
    public Object loadFromProperties(Properties properties) {
        return null;
    }
}
