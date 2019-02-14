package com.fr.swift.config.dao;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.converter.FindList;

import java.sql.SQLException;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftServiceInfoDao extends SwiftConfigDao<SwiftServiceInfoBean> {
    FindList<SwiftServiceInfoBean> getServiceInfoByService(ConfigSession session, String service);

    FindList<SwiftServiceInfoBean> getServiceInfoBySelective(ConfigSession session, SwiftServiceInfoBean bean);

    boolean deleteByServiceInfo(ConfigSession session, String serviceInfo) throws SQLException;
}
