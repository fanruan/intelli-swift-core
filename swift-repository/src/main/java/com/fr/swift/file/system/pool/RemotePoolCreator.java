package com.fr.swift.file.system.pool;

import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.repository.config.FtpRepositoryConfig;
import com.fr.swift.repository.config.HdfsRepositoryConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/7/5
 */
public class RemotePoolCreator {
    private static RemotePoolCreator creator;
    private Map<SwiftFileSystemConfig, BaseRemoteSystemPool> poolMap = new ConcurrentHashMap<SwiftFileSystemConfig, BaseRemoteSystemPool>();

    public static RemotePoolCreator creator() {
        if (null == creator) {
            synchronized (RemotePoolCreator.class) {
                if (null == creator) {
                    creator = new RemotePoolCreator();
                }
            }
        }
        return creator;
    }

    public BaseRemoteSystemPool getPool(SwiftFileSystemConfig config) {
        if (null == poolMap.get(config)) {
            switch (config.getType()) {
                case FTP:
                    poolMap.put(config, new FtpFileSystemPool((FtpRepositoryConfig) config));
                    break;
                case HDFS:
                    poolMap.put(config, new HdfsFileSystemPool((HdfsRepositoryConfig) config));
                    break;
                case FR:
                    return null;
            }
        }
        return poolMap.get(config);
    }
}
