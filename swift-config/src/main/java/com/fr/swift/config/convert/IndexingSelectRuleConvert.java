package com.fr.swift.config.convert;

import com.fr.swift.config.bean.IndexingSelectRule;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.context.SwiftContext;
import com.fr.third.org.hibernate.Session;

/**
 * @author yee
 * @date 2018/7/16
 */
public class IndexingSelectRuleConvert extends AbstractObjectConfigConvert<IndexingSelectRule> {
    private static final String INDEXING_SELECT_RULE = "INDEXING_SELECT_RULE";

    @Override
    public IndexingSelectRule toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) {
        try {
            return super.toBean(dao, session, args);
        } catch (Exception e) {
            return SwiftContext.get().getBean("defaultIndexingSelectRule", IndexingSelectRule.class);
        }
    }

    @Override
    protected String getNameSpace() {
        return INDEXING_SELECT_RULE;
    }
}
