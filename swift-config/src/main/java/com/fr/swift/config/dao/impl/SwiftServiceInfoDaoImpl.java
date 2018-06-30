package com.fr.swift.config.dao.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Criterion;
import com.fr.third.org.hibernate.criterion.Restrictions;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public class SwiftServiceInfoDaoImpl extends BasicDao<SwiftServiceInfoEntity> implements SwiftServiceInfoDao {

    public SwiftServiceInfoDaoImpl() {
        super(SwiftServiceInfoEntity.class);
    }

    @Override
    public List<SwiftServiceInfoEntity> getServiceInfoByService(Session session, String service) {
        return find(session, Restrictions.eq("service", service));
    }

    @Override
    public List<SwiftServiceInfoEntity> getServiceInfoBySelective(Session session, SwiftServiceInfoBean bean) {
        List<Criterion> list = new ArrayList<Criterion>();
        if (StringUtils.isNotEmpty(bean.getClusterId())) {
            list.add(Restrictions.eq("clusterId", bean.getClusterId()));
        }
        if (StringUtils.isNotEmpty(bean.getService())) {
            list.add(Restrictions.eq("service", bean.getClusterId()));
        }
        if (StringUtils.isNotEmpty(bean.getServiceInfo())) {
            list.add(Restrictions.eq("serviceInfo", bean.getClusterId()));
        }
        return find(session, list.toArray(new Criterion[list.size()]));
    }

    @Override
    public boolean deleteByServiceInfo(Session session, String serviceInfo) throws SQLException {
        try {
            List<SwiftServiceInfoEntity> entities = find(session, Restrictions.eq("serviceInfo", serviceInfo));
            for (SwiftServiceInfoEntity entity : entities) {
                session.delete(entity);
            }
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

}
