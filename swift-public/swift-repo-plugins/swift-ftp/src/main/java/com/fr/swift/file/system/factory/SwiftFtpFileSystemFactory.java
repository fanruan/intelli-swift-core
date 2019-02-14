package com.fr.swift.file.system.factory;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.file.system.annotation.FileSystemFactory;
import com.fr.swift.file.system.impl.FtpFileSystemImpl;
import com.fr.swift.file.system.pool.FtpFileSystemPool;
import com.fr.swift.file.system.pool.RemoteFileSystemPool;
import com.fr.swift.repository.config.FtpRepositoryConfig;
import com.fr.swift.util.Strings;

import java.util.Properties;


/**
 * @author yee
 * @date 2018/8/21
 */
@FileSystemFactory(name = "FTP")
@SwiftBean(name = "FTP")
public class SwiftFtpFileSystemFactory extends BasePooledFileSystemFactory<FtpFileSystemImpl, FtpRepositoryConfig> {

    @Override
    protected RemoteFileSystemPool<FtpFileSystemImpl> createPool(FtpRepositoryConfig config) {
        return new FtpFileSystemPool(config);
    }

    @Override
    public FtpRepositoryConfig loadFromProperties(Properties properties) {
        String host = properties.getProperty("repo.host", "127.0.0.1");
        String user = properties.getProperty("repo.user", "anonymous");
        String pass = properties.getProperty("repo.pass", Strings.EMPTY);
        String protocol = properties.getProperty("repo.protocol", "FTP");
        String charset = properties.getProperty("repo.charset", "UTF-8");
        String port = properties.getProperty("repo.port", "21");
        String root = properties.getProperty("repo.root", "/");
        FtpRepositoryConfig config = new FtpRepositoryConfig();
        config.setProtocol(protocol);
        config.setRootPath(root);
        config.setCharset(charset);
        config.setHost(host);
        config.setPassword(pass);
        config.setUsername(user);
        config.setPort(port);
        return config;
    }
}
