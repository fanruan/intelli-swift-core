package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.DataSyncRule;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.command.SwiftConfigEntityCommandBus;
import com.fr.swift.config.command.impl.SwiftConfigEntityCommandBusImpl;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.config.query.impl.SwiftConfigEntityQueryBusImpl;
import com.fr.swift.config.service.DataSyncRuleService;

/**
 * @author yee
 * @date 2018/7/16
 */
@SwiftBean(name = "dataSyncRuleService")
public class DataSyncRuleServiceImpl implements DataSyncRuleService {
    private SwiftConfigEntityCommandBus commandBus = new SwiftConfigEntityCommandBusImpl();
    private SwiftConfigEntityQueryBus queryBus = new SwiftConfigEntityQueryBusImpl();

    @Override
    public DataSyncRule getCurrentRule() {
        final DataSyncRule defaultDataSyncRule = SwiftContext.get().getBean("defaultDataSyncRule", DataSyncRule.class);
        return queryBus.select(SwiftConfigConstants.Namespace.DATA_SYNC_RULE, DataSyncRule.class, defaultDataSyncRule);
    }

    @Override
    public void setCurrentRule(DataSyncRule rule) {
        commandBus.delete(SwiftConfigConstants.Namespace.DATA_SYNC_RULE);
        commandBus.merge(SwiftConfigConstants.Namespace.DATA_SYNC_RULE, rule);
    }
}
