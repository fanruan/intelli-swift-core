package com.fr.swift.config.conf.dao.impl;

import com.fr.swift.config.conf.bean.SwiftMetaDataBean;
import com.fr.swift.config.conf.dao.BaseDAO;
import com.fr.swift.config.conf.dao.SwiftMetaDataDAO;
import com.fr.swift.config.conf.entity.MetaDataEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * @author yee
 * @date 2018/5/24
 */
public class SwiftMetaDataDAOImpl extends BaseDAO<MetaDataEntity> implements SwiftMetaDataDAO {

    private static final String FIND_BY_NAME_HQL = String.format("from MetaDataEntity entity where entity.%s = ", SwiftMetaDataBean.COLUMN_TABLE_NAME);

    public SwiftMetaDataDAOImpl() {
        super(MetaDataEntity.class);
    }

    @Override
    public SwiftMetaDataBean findBySourceKey(String sourceKey) {
        return select(sourceKey).convert();
    }

    @Override
    public SwiftMetaDataBean findByTableName(String tableName) {
        List<MetaDataEntity> list = find(String.format("%s '%s'", FIND_BY_NAME_HQL, tableName));
        if (null == list && list.isEmpty()) {
            throw new RuntimeException(String.format("Find meta data error! Table named '%s' not exists!", tableName));
        }
        if (list.size() != 1) {
            throw new RuntimeException(String.format("Find meta data error! Table named '%s' is duplicate!", tableName));
        }
        return list.get(0).convert();
    }

    @Override
    public boolean addOrUpdateSwiftMetaData(SwiftMetaDataBean metaDataBean) {
        return saveOrUpdate(metaDataBean.convert());
    }

    @Override
    public boolean deleteSwiftMetaDataBean(String sourceKey) {
        return deleteById(sourceKey);
    }

    @Override
    public List<SwiftMetaDataBean> findAll() {
        List<MetaDataEntity> all = find();
        List<SwiftMetaDataBean> result = new ArrayList<SwiftMetaDataBean>();
        for (MetaDataEntity entity : all) {
            result.add(entity.convert());
        }
        return result;
    }
}
