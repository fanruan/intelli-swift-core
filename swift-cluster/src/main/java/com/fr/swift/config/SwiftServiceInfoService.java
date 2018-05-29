package com.fr.swift.config;

import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftServiceInfoService {

    void saveOrUpdateServiceInfo(SwiftServiceInfoBean serviceInfoBean) throws SQLException;

    void removeServiceInfo(SwiftServiceInfoBean serviceInfoBean) throws SQLException;

    SwiftServiceInfoBean getServiceInfo(SwiftServiceInfoBean serviceInfoBean) throws SQLException;

    List<SwiftServiceInfoBean> getAllServiceInfo();

    List<SwiftServiceInfoBean> getServiceInfoByService(String service);
}
