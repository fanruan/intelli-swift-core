package com.fr.swift.config.convert;

import com.fr.swift.config.bean.DataSyncRule;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.context.SwiftContext;
import com.fr.third.org.hibernate.Session;

/**
 * @author yee
 * @date 2018/7/16
 */
public class DataSyncRuleConvert extends AbstractObjectConfigConvert<DataSyncRule> {

    private static final String DATA_SYNC_RULE_KEY = "DATA_SYNC_RULE";

    @Override
    public DataSyncRule toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) {
        try {
            return super.toBean(dao, session, args);
        } catch (Exception e) {
            return SwiftContext.get().getBean("defaultDataSyncRule", DataSyncRule.class);
        }
    }

    @Override
    protected String transferClassName(String className) {
        return className;
    }

    @Override
    protected String getNameSpace() {
        return DATA_SYNC_RULE_KEY;
    }
}
