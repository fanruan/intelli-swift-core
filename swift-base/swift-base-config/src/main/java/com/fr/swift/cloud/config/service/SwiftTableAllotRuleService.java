package com.fr.swift.cloud.config.service;

import com.fr.swift.cloud.annotation.service.DbService;
import com.fr.swift.cloud.config.entity.SwiftTableAllotRule;
import com.fr.swift.cloud.source.SourceKey;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
@DbService
public interface SwiftTableAllotRuleService {

    SwiftTableAllotRule getByTale(SourceKey sourceKey);

    void save(SwiftTableAllotRule SwiftTableAllotRule);

    void update(SwiftTableAllotRule SwiftTableAllotRule);

}