package com.fr.swift.config.dao.impl;

import com.fr.config.dao.HibernateTemplate;
import com.fr.stable.StringUtils;
import com.fr.stable.db.DBSession;
import com.fr.store.access.AccessActionCallback;
import com.fr.store.access.ResourceHolder;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.BaseDao;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.util.Crasher;
import com.fr.third.org.hibernate.Query;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public class SwiftServiceInfoDaoImpl extends BaseDao<SwiftServiceInfoEntity> implements SwiftServiceInfoDao {

    public SwiftServiceInfoDaoImpl() {
        super(SwiftServiceInfoEntity.class);
    }

    @Override
    public List<SwiftServiceInfoEntity> getServiceInfoByService(String service) {
        String sql = "from " + entityClass.getSimpleName() + " entity where entity.service = '" + service + "'";
        return find(sql);
    }

    @Override
    public List<SwiftServiceInfoEntity> getServiceInfoBySelective(SwiftServiceInfoBean bean) {
        StringBuffer sql = new StringBuffer("from " + entityClass.getSimpleName() + " entity where 1 = 1 ");
        if (StringUtils.isNotEmpty(bean.getClusterId())) {
            sql.append(" and entity.clusterId = '").append(bean.getClusterId()).append("' ");
        }
        if (StringUtils.isNotEmpty(bean.getService())) {
            sql.append(" and entity.service = '").append(bean.getService()).append("' ");
        }
        if (StringUtils.isNotEmpty(bean.getServiceInfo())) {
            sql.append(" and entity.serviceInfo = '").append(bean.getServiceInfo()).append("' ");
        }
        return find(sql.toString());
    }

    @Override
    public boolean deleteByServiceInfo(String serviceInfo) throws SQLException {
        final String sql = "delete from " + entityClass.getSimpleName() + " entity where entity.serviceInfo = '" + serviceInfo + "'";
        try {
            return HibernateTemplate.getInstance().doQuery(new AccessActionCallback<Boolean>() {
                public Boolean doInServer(ResourceHolder holder) {
                    DBSession session = (DBSession) holder.getResource();

                    try {
                        Query query = session.createHibernateQuery(sql);
                        query.executeUpdate();
                    } catch (Exception e) {
                        return Crasher.crash("deleteById failed", e);
                    }

                    return true;
                }
            });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
