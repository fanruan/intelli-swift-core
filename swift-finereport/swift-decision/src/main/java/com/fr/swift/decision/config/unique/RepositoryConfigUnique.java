package com.fr.swift.decision.config.unique;


import com.fr.swift.config.convert.ObjectConverter;
import com.fr.swift.repository.SwiftFileSystemConfig;

/**
 * @author yee
 * @date 2018/6/15
 */
public interface RepositoryConfigUnique extends ObjectConverter<SwiftFileSystemConfig> {
    String getNameSpace();
}
