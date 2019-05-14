package com.fr.swift.config.dao.impl;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftMetaDataDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;

import java.sql.SQLException;
import java.util.List;


/**
 * @author yee
 * @date 2018/5/24
 */
@SwiftBean
public class SwiftMetaDataDaoImpl extends BasicDao<SwiftMetaDataBean> implements SwiftMetaDataDao {
    public SwiftMetaDataDaoImpl() {
        super(SwiftMetaDataBean.class);
    }

    @Override
    public SwiftMetaDataBean findBySourceKey(ConfigSession session, String sourceKey) throws SQLException {
        return select(session, sourceKey);
    }

    @Override
    public SwiftMetaDataBean findByTableName(ConfigSession session, String tableName) {
        List<SwiftMetaDataBean> list = find(session, ConfigWhereImpl.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_TABLE_NAME, tableName));
        if (null == list || list.isEmpty()) {
            throw new RuntimeException(String.format("Find meta data error! Table named '%s' not exists!", tableName));
        }
        if (list.size() != 1) {
            throw new RuntimeException(String.format("Find meta data error! Table named '%s' is duplicate!", tableName));
        }
        return list.get(0);
    }

    @Override
    public boolean addOrUpdateSwiftMetaData(ConfigSession session, SwiftMetaDataBean metaDataBean) throws SQLException {
        return saveOrUpdate(session, metaDataBean);
    }

    @Override
    public boolean deleteSwiftMetaDataBean(ConfigSession session, String sourceKey) throws SQLException {
        return deleteById(session, sourceKey);
    }

    @Override
    public List<SwiftMetaDataBean> findAll(ConfigSession session) {
        return find(session);
    }

    @Override
    public List<SwiftMetaDataBean> fuzzyFind(ConfigSession session, String fuzzyName) {
        ConfigWhere configWhere = ConfigWhereImpl.like("tableName", fuzzyName, ConfigWhere.MatchMode.ANY);
        return find(session, configWhere);
    }
}
