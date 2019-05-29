package com.fr.swift.config.dao;

import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.oper.ConfigSession;

import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftServiceInfoDao extends SwiftConfigDao<SwiftServiceInfoEntity> {
    List<SwiftServiceInfoEntity> getServiceInfoByService(ConfigSession session, String service);

    List<SwiftServiceInfoEntity> getServiceInfoBySelective(ConfigSession session, SwiftServiceInfoEntity bean);

    boolean deleteByServiceInfo(ConfigSession session, String serviceInfo) throws SQLException;
}
