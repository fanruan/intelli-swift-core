package com.fr.swift.config.convert;

import com.fr.swift.config.bean.IndexingSelectRule;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.service.handler.indexing.rule.DefaultIndexingSelectRule;
import com.fr.third.org.hibernate.Session;

/**
 * @author yee
 * @date 2018/7/16
 */
public class IndexingSelectRuleConvert extends AbstractConfigConvert<IndexingSelectRule> {
    private static final String INDEXING_SELECT_RULE = "INDEXING_SELECT_RULE";

    @Override
    public IndexingSelectRule toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) {
        try {
            return super.toBean(dao, session, args);
        } catch (Exception e) {
            return new DefaultIndexingSelectRule();
        }
    }

    @Override
    protected String getNameSpace() {
        return INDEXING_SELECT_RULE;
    }
}
