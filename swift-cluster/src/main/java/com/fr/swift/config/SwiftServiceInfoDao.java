package com.fr.swift.config;

import com.fr.swift.config.dao.SwiftConfigDAO;

import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftServiceInfoDao extends SwiftConfigDAO<SwiftServiceInfoEntity> {
    List<SwiftServiceInfoEntity> getServiceInfoByService(String service);
}
