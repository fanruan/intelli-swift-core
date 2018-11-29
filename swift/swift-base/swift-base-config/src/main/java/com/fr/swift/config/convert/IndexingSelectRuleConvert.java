package com.fr.swift.config.convert;

import com.fr.swift.config.bean.IndexingSelectRule;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.context.SwiftContext;

/**
 * @author yee
 * @date 2018/7/16
 */
public class IndexingSelectRuleConvert extends AbstractObjectConfigConvert<IndexingSelectRule> {
    private static final String INDEXING_SELECT_RULE = "INDEXING_SELECT_RULE";

    @Override
    public IndexingSelectRule toBean(SwiftConfigDao<SwiftConfigBean> dao, ConfigSession session, Object... args) {
        try {
            return super.toBean(dao, session, args);
        } catch (Exception e) {
            return SwiftContext.get().getBean("defaultIndexingSelectRule", IndexingSelectRule.class);
        }
    }

    @Override
    protected String transferClassName(String className) {
        return className;
    }

    @Override
    protected String getNameSpace() {
        return INDEXING_SELECT_RULE;
    }
}
