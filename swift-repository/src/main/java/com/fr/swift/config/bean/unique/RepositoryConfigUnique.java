package com.fr.swift.config.bean.unique;


import com.fr.swift.config.bean.Convert;
import com.fr.swift.config.bean.SwiftFileSystemConfig;

/**
 * @author yee
 * @date 2018/6/15
 */
public interface RepositoryConfigUnique extends Convert<SwiftFileSystemConfig> {
    String getNameSpace();
}
