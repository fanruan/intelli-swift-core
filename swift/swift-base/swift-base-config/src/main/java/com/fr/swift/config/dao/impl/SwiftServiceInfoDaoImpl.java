package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.RestrictionFactory;
import com.fr.swift.config.oper.impl.RestrictionFactoryImpl;
import com.fr.swift.converter.FindList;
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
public class SwiftServiceInfoDaoImpl extends BasicDao<SwiftServiceInfoBean> implements SwiftServiceInfoDao {

    public SwiftServiceInfoDaoImpl() {
        super(SwiftServiceInfoBean.TYPE, RestrictionFactoryImpl.INSTANCE);
    }

    public SwiftServiceInfoDaoImpl(RestrictionFactory factory) {
        super(SwiftServiceInfoBean.TYPE, factory);
    }

    @Override
    public FindList<SwiftServiceInfoBean> getServiceInfoByService(ConfigSession session, String service) {
        return find(session, factory.eq("service", service));
    }

    @Override
    public FindList<SwiftServiceInfoBean> getServiceInfoBySelective(ConfigSession session, SwiftServiceInfoBean bean) {
        List list = new ArrayList();
        if (Strings.isNotEmpty(bean.getClusterId())) {
            list.add(factory.eq("clusterId", bean.getClusterId()));
        }
        if (Strings.isNotEmpty(bean.getService())) {
            list.add(factory.eq("service", bean.getService()));
        }
        if (Strings.isNotEmpty(bean.getServiceInfo())) {
            list.add(factory.eq("serviceInfo", bean.getServiceInfo()));
        }
        return find(session, list.toArray());
    }

    @Override
    public boolean deleteByServiceInfo(final ConfigSession session, String serviceInfo) throws SQLException {
        try {
            find(session, factory.eq("serviceInfo", serviceInfo)).justForEach(new FindList.ConvertEach() {
                @Override
                public Object forEach(int idx, Object item) {
                    session.delete(item);
                    return null;
                }
            });
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

}
