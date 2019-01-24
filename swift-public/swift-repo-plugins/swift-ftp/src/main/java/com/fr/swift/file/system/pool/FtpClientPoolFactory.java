package com.fr.swift.file.system.pool;

import com.fr.swift.file.client.SwiftFTPClient;
import com.fr.swift.file.client.impl.FTPClientImpl;
import com.fr.swift.file.client.impl.SFTPClientImpl;
import com.fr.swift.repository.config.FtpRepositoryConfig;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author yee
 * @date 2018-12-03
 */
public class FtpClientPoolFactory implements PooledObjectFactory<SwiftFTPClient> {
    private FtpRepositoryConfig config;

    public FtpClientPoolFactory(FtpRepositoryConfig config) {
        this.config = config;
    }

    @Override
    public PooledObject<SwiftFTPClient> makeObject() throws Exception {
        SwiftFTPClient client;
        if ("FTP".equalsIgnoreCase(this.config.getProtocol())) {
            client = new FTPClientImpl(this.config);
        } else {
            if (!"SFTP".equalsIgnoreCase(this.config.getProtocol())) {
                throw new UnsupportedOperationException("Only FTP & SFTP are provided for ftp connection!");
            }

            client = new SFTPClientImpl(this.config);
        }

        if (client.login()) {
            return new DefaultPooledObject<SwiftFTPClient>(client);
        } else {
            throw new Exception(client.getClass().getName() + " login failed!");
        }
    }

    @Override
    public void destroyObject(PooledObject<SwiftFTPClient> pooledObject) throws Exception {
        SwiftFTPClient client = pooledObject.getObject();
        if (client != null) {
            try {
                if (client.isConnected()) {
                    client.close();
                }
            } finally {
                client.disconnect();
            }

        }
    }

    @Override
    public boolean validateObject(PooledObject<SwiftFTPClient> pooledObject) {
        SwiftFTPClient client = pooledObject.getObject();

        try {
            return client != null && client.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<SwiftFTPClient> poolObject) throws Exception {
    }

    @Override
    public void passivateObject(PooledObject<SwiftFTPClient> pooledObject) throws Exception {
    }
}