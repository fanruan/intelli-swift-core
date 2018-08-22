package com.fr.swift.config.convert;

import com.fr.general.ComparatorUtils;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;

/**
 * @author yee
 * @date 2018/7/6
 */
public class SwiftFileSystemConvert extends AbstractObjectConfigConvert<SwiftFileSystemConfig> {

    private static final String OLD_FTP_CONF = "com.fr.swift.config.bean.FtpRepositoryConfigBean";
    private static final String OLD_HDFS_CONF = "com.fr.swift.config.bean.HdfsRepositoryConfigBean";

    @Override
    protected String transferClassName(String className) {
        if (ComparatorUtils.equals(className, OLD_HDFS_CONF)) {
            return "com.fr.swift.repository.config.HdfsRepositoryConfig";
        } else if (ComparatorUtils.equals(className, OLD_FTP_CONF)) {
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
