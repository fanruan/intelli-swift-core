package com.fr.swift.config;

import com.fr.swift.config.dao.BaseDAO;

import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServiceInfoDaoImpl extends BaseDAO<SwiftServiceInfoEntity> implements SwiftServiceInfoDao {

    public SwiftServiceInfoDaoImpl() {
        super(SwiftServiceInfoEntity.class);
    }

    @Override
    public List<SwiftServiceInfoEntity> getServiceInfoByService(String service) {
        String sql = "from " + entityClass.getSimpleName() + " entity where entity.service = '" + service + "'";
        return find(sql);
    }
}
