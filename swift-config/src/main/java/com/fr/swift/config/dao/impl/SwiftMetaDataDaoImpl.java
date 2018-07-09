package com.fr.swift.config.dao.impl;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftMetaDataDao;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Conjunction;
import com.fr.third.org.hibernate.criterion.Restrictions;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yee
 * @date 2018/5/24
 */
@Service
public class SwiftMetaDataDaoImpl extends BasicDao<SwiftMetaDataEntity> implements SwiftMetaDataDao {

    public SwiftMetaDataDaoImpl() {
        super(SwiftMetaDataEntity.class);
    }

    @Override
    public SwiftMetaDataBean findBySourceKey(Session session, String sourceKey) throws SQLException {
        return select(session, sourceKey).convert();
    }

    @Override
    public SwiftMetaDataBean findByTableName(Session session, String tableName) {
        List<SwiftMetaDataEntity> list = find(session, Restrictions.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_TABLE_NAME, tableName));
        if (null == list || list.isEmpty()) {
            throw new RuntimeException(String.format("Find meta data error! Table named '%s' not exists!", tableName));
        }
        if (list.size() != 1) {
            throw new RuntimeException(String.format("Find meta data error! Table named '%s' is duplicate!", tableName));
        }
        return list.get(0).convert();
    }

    @Override
    public boolean addOrUpdateSwiftMetaData(Session session, SwiftMetaDataBean metaDataBean) throws SQLException {
        return saveOrUpdate(session, metaDataBean.convert());
    }

    @Override
    public boolean deleteSwiftMetaDataBean(Session session, String sourceKey) throws SQLException {
        return deleteById(session, sourceKey);
    }

    @Override
    public List<SwiftMetaDataBean> findAll(Session session) {
        List<SwiftMetaDataEntity> all = find(session, new Conjunction[]{});
        List<SwiftMetaDataBean> result = new ArrayList<SwiftMetaDataBean>();
        for (SwiftMetaDataEntity entity : all) {
            result.add(entity.convert());
        }
        return result;
    }
}
