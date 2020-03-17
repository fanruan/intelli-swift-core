package com.fr.swift.config.service;

import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.source.SourceKey;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
public interface SwiftTableAllotRuleService {

    SwiftTableAllotRule getByTale(SourceKey sourceKey);

    void save(SwiftTableAllotRule swiftTableAllotRule);

}