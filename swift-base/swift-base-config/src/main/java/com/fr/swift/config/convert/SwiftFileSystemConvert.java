package com.fr.swift.config.convert;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;
import com.fr.swift.repository.PackageConnectorConfig;

/**
 * @author yee
 * @date 2018/7/6
 */
public class SwiftFileSystemConvert extends AbstractObjectConfigConvert<PackageConnectorConfig> {

    private static final String OLD_FTP_CONF = "com.fr.swift.config.bean.FtpRepositoryConfigBean";
    private static final String OLD_HDFS_CONF = "com.fr.swift.config.bean.HdfsRepositoryConfigBean";

    public SwiftFileSystemConvert() {
        super(PackageConnectorConfig.class);
    }

    @Override
    protected String transferClassName(String className) {
        if (OLD_HDFS_CONF.equals(className)) {
            return "com.fr.swift.repository.config.HdfsRepositoryConfig";
        } else if (OLD_FTP_CONF.equals(className)) {
            return "com.fr.swift.repository.config.FtpRepositoryConfig";
        } else {
            return className;
        }
    }

    @Override
    protected String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.REPOSITORY_CONF_NAMESPACE;
    }
}
