package com.fr.swift.config.convert;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.DataSyncRule;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/7/16
 */
public class DataSyncRuleConvert extends AbstractObjectConfigConvert<DataSyncRule> {

    private static final String DATA_SYNC_RULE_KEY = "DATA_SYNC_RULE";

    public DataSyncRuleConvert() {
        super(DataSyncRule.class);
    }

    @Override
    public DataSyncRule toBean(SwiftConfigDao<SwiftConfigBean> dao, ConfigSession session, Object... args) throws SQLException {
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
