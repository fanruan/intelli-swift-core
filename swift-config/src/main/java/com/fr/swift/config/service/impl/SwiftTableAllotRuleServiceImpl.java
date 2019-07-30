package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftTableAllotRuleDao;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigSessionCreator;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;

import java.sql.SQLException;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
@SwiftBean
public class SwiftTableAllotRuleServiceImpl implements SwiftTableAllotRuleService {
    private SwiftTableAllotRuleDao swiftTableAllotRuleDao;
    private ConfigSessionCreator configSessionCreator;

    public SwiftTableAllotRuleServiceImpl() {
        this.swiftTableAllotRuleDao = SwiftContext.get().getBean(SwiftTableAllotRuleDao.class);
        this.configSessionCreator = SwiftContext.get().getBean(ConfigSessionCreator.class);
    }

    @Override
    public SwiftTableAllotRule getAllotRuleByTable(final SourceKey sourceKey) {
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<SwiftTableAllotRule>(false) {
                @Override
                public SwiftTableAllotRule work(ConfigSession session) throws SQLException {
                    SwiftTableAllotRule swiftTableAllotRule = swiftTableAllotRuleDao.select(session, sourceKey.getId());
                    return swiftTableAllotRule;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("getAllotRuleByTable {} error!", sourceKey.getId(), e);
            return null;
        }
    }

    @Override
    public boolean saveAllotRule(final SwiftTableAllotRule swiftTableAllotRule) {
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    return swiftTableAllotRuleDao.saveOrUpdate(session, swiftTableAllotRule);
                }

            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("saveAllotRule {} error!", swiftTableAllotRule.toString(), e);
            return false;
        }
    }
}