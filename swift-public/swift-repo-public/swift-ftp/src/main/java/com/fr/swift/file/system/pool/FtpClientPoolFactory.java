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

    public FtpClientPoolFactory(FtpRepositoryConfig var1) {
        this.config = var1;
    }

    @Override
    public PooledObject<SwiftFTPClient> makeObject() throws Exception {
        Object var1;
        if ("FTP".equalsIgnoreCase(this.config.getProtocol())) {
            var1 = new FTPClientImpl(this.config);
        } else {
            if (!"SFTP".equalsIgnoreCase(this.config.getProtocol())) {
                throw new UnsupportedOperationException("Only FTP & SFTP are provided for ftp connection!");
            }

            var1 = new SFTPClientImpl(this.config);
        }

        if (((SwiftFTPClient) var1).login()) {
            return new DefaultPooledObject(var1);
        } else {
            throw new Exception(var1.getClass().getName() + " login failed!");
        }
    }

    @Override
    public void destroyObject(PooledObject<SwiftFTPClient> var1) throws Exception {
        SwiftFTPClient var2 = var1.getObject();
        if (var2 != null) {
            try {
                if (var2.isConnected()) {
                    var2.close();
                }
            } finally {
                var2.disconnect();
            }

        }
    }

    @Override
    public boolean validateObject(PooledObject<SwiftFTPClient> var1) {
        SwiftFTPClient var2 = var1.getObject();

        try {
            return var2 != null && var2.isConnected();
        } catch (Exception var4) {
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<SwiftFTPClient> var1) throws Exception {
    }

    @Override
    public void passivateObject(PooledObject<SwiftFTPClient> var1) throws Exception {
    }
}