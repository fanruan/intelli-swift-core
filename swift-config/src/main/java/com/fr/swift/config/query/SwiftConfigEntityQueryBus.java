package com.fr.swift.config.query;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.entity.SwiftConfigEntity;

/**
 * @author yee
 * @date 2019-08-07
 */
public interface SwiftConfigEntityQueryBus extends SwiftConfigQueryBus<SwiftConfigEntity> {
    <T> T select(SwiftConfigConstants.Namespace namespace, Class<T> tClass, T defaultValue);
}
