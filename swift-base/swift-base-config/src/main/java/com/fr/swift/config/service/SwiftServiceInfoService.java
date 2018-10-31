package com.fr.swift.config.service;

import com.fr.swift.config.bean.SwiftServiceInfoBean;

import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftServiceInfoService extends ConfigService<SwiftServiceInfoBean> {

    String SERVICE = "cluster_master_service";

    boolean removeServiceInfo(SwiftServiceInfoBean serviceInfoBean);

    SwiftServiceInfoBean getServiceInfo(SwiftServiceInfoBean serviceInfoBean);

    List<SwiftServiceInfoBean> getAllServiceInfo();

    List<SwiftServiceInfoBean> getServiceInfoByService(String service);
}
