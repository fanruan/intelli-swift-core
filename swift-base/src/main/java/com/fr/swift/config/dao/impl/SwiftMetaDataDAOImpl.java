package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.dao.BaseDAO;
import com.fr.swift.config.dao.SwiftMetaDataDAO;
import com.fr.swift.config.entity.SwiftMetaDataEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yee
 * @date 2018/5/24
 */
public class SwiftMetaDataDAOImpl extends BaseDAO<SwiftMetaDataEntity> implements SwiftMetaDataDAO {

    private static final String FIND_BY_NAME_HQL = String.format("from SwiftMetaDataEntity entity where entity.%s = ", SwiftMetaDataBean.COLUMN_TABLE_NAME);

    public SwiftMetaDataDAOImpl() {
        super(SwiftMetaDataEntity.class);
    }

    @Override
    public SwiftMetaDataBean findBySourceKey(String sourceKey) throws SQLException {
        return select(sourceKey).convert();
    }

    @Override
    public SwiftMetaDataBean findByTableName(String tableName) {
        List<SwiftMetaDataEntity> list = find(String.format("%s '%s'", FIND_BY_NAME_HQL, tableName));
        if (null == list && list.isEmpty()) {
            throw new RuntimeException(String.format("Find meta data error! Table named '%s' not exists!", tableName));
        }
        if (list.size() != 1) {
            throw new RuntimeException(String.format("Find meta data error! Table named '%s' is duplicate!", tableName));
        }
        return list.get(0).convert();
    }

    @Override
    public boolean addOrUpdateSwiftMetaData(SwiftMetaDataBean metaDataBean) throws SQLException {
        return saveOrUpdate(metaDataBean.convert());
    }

    @Override
    public boolean deleteSwiftMetaDataBean(String sourceKey) throws SQLException {
        return deleteById(sourceKey);
    }

    @Override
    public List<SwiftMetaDataBean> findAll() {
        List<SwiftMetaDataEntity> all = find();
        List<SwiftMetaDataBean> result = new ArrayList<SwiftMetaDataBean>();
        for (SwiftMetaDataEntity entity : all) {
            result.add(entity.convert());
        }
        return result;
    }
}
