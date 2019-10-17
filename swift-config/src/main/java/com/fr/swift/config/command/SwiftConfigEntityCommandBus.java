package com.fr.swift.config.command;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.entity.SwiftConfigEntity;

/**
 * @author yee
 * @date 2019-08-07
 */
public interface SwiftConfigEntityCommandBus extends SwiftConfigCommandBus<SwiftConfigEntity> {
    void merge(SwiftConfigConstants.Namespace namespace, Object obj);

    void delete(SwiftConfigConstants.Namespace key);
}
