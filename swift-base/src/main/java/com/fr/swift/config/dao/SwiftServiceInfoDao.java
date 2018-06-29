package com.fr.swift.config.dao;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.third.org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftServiceInfoDao {
    List<SwiftServiceInfoEntity> getServiceInfoByService(Session session, String service);

    List<SwiftServiceInfoEntity> getServiceInfoBySelective(Session session, SwiftServiceInfoBean bean);

    boolean deleteByServiceInfo(Session session, String serviceInfo) throws SQLException;
}
