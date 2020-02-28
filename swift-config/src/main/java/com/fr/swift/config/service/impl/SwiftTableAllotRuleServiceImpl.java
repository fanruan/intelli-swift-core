package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.command.impl.SwiftHibernateConfigCommandBus;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.query.SwiftConfigQueryBus;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.source.SourceKey;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
@SwiftBean
public class SwiftTableAllotRuleServiceImpl implements SwiftTableAllotRuleService {
    private SwiftConfigCommandBus<SwiftTableAllotRule> commandBus = new SwiftHibernateConfigCommandBus<>(SwiftTableAllotRule.class);
    private SwiftConfigQueryBus<SwiftTableAllotRule> queryBus = new SwiftHibernateConfigQueryBus<>(SwiftTableAllotRule.class);


    @Override
    public SwiftTableAllotRule getAllotRuleByTable(final SourceKey sourceKey) {
        return queryBus.select(sourceKey.getId());
    }

    @Override
    public boolean saveAllotRule(final SwiftTableAllotRule swiftTableAllotRule) {
        commandBus.merge(swiftTableAllotRule);
        return true;
    }
}