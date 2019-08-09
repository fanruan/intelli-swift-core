package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SegmentDestSelectRule;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.command.SwiftConfigEntityCommandBus;
import com.fr.swift.config.command.impl.SwiftConfigEntityCommandBusImpl;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.config.query.impl.SwiftConfigEntityQueryBusImpl;
import com.fr.swift.config.service.SegmentDestSelectRuleService;

/**
 * @author yee
 * @date 2018/7/16
 */
@SwiftBean(name = "segmentDestSelectRuleService")
public class SegDestSelectRuleServiceImpl implements SegmentDestSelectRuleService {
    private SwiftConfigEntityCommandBus commandBus = new SwiftConfigEntityCommandBusImpl();
    private SwiftConfigEntityQueryBus queryBus = new SwiftConfigEntityQueryBusImpl();

    @Override
    public SegmentDestSelectRule getCurrentRule() {
        final SegmentDestSelectRule defaultDataSyncRule = SwiftContext.get().getBean("defaultSegmentDestSelectRule", SegmentDestSelectRule.class);
        return queryBus.select(SwiftConfigConstants.Namespace.SEGMENT_DEST_SELECT_RULE, SegmentDestSelectRule.class, defaultDataSyncRule);
    }

    @Override
    public void setCurrentRule(SegmentDestSelectRule rule) {
        commandBus.delete(SwiftConfigConstants.Namespace.SEGMENT_DEST_SELECT_RULE);
        commandBus.merge(SwiftConfigConstants.Namespace.SEGMENT_DEST_SELECT_RULE, rule);
    }
}
