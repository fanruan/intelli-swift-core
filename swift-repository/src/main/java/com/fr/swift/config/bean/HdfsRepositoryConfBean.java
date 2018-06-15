package com.fr.swift.config.bean;

import com.fr.config.utils.UniqueKey;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.file.conf.impl.HdfsRepositoryConfigImpl;

/**
 * @author yee
 * @date 2018/6/15
 */
public class HdfsRepositoryConfBean extends UniqueKey implements RepositoryConfBean<HdfsRepositoryConfigImpl> {
    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.HDFS_REPOSITORY_NAMESPACE;
    }

    @Override
    public HdfsRepositoryConfigImpl convert() {
        return null;
    }
}
