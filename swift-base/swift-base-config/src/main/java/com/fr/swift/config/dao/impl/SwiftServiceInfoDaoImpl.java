package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.util.Strings;

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
@SwiftBean
public class SwiftServiceInfoDaoImpl extends BasicDao<SwiftServiceInfoEntity> implements SwiftServiceInfoDao {

    public SwiftServiceInfoDaoImpl() {
        super(SwiftServiceInfoEntity.class);
    }

    @Override
    public List<SwiftServiceInfoEntity> getServiceInfoByService(ConfigSession session, String service) {
        return find(session, ConfigWhereImpl.eq("service", service));
    }

    @Override
    public List<SwiftServiceInfoEntity> getServiceInfoBySelective(ConfigSession session, SwiftServiceInfoEntity bean) {
        List<ConfigWhere> list = new ArrayList<ConfigWhere>();
        if (Strings.isNotEmpty(bean.getClusterId())) {
            list.add(ConfigWhereImpl.eq("clusterId", bean.getClusterId()));
        }
        if (Strings.isNotEmpty(bean.getService())) {
            list.add(ConfigWhereImpl.eq("service", bean.getService()));
        }
        if (Strings.isNotEmpty(bean.getServiceInfo())) {
            list.add(ConfigWhereImpl.eq("serviceInfo", bean.getServiceInfo()));
        }
        return find(session, list.toArray(new ConfigWhere[0]));
    }

    @Override
    public boolean deleteByServiceInfo(final ConfigSession session, String serviceInfo) throws SQLException {
        try {
            for (SwiftServiceInfoEntity info : find(session, ConfigWhereImpl.eq("serviceInfo", serviceInfo))) {
                session.delete(info);
            }
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

}
