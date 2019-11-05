package com.fr.swift.config.service;

import com.fr.swift.config.entity.SwiftServiceInfoEntity;

import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftServiceInfoService extends ConfigService<SwiftServiceInfoEntity> {

    String SERVICE = "cluster_master_service";

    boolean removeServiceInfo(SwiftServiceInfoEntity serviceInfoBean);

    SwiftServiceInfoEntity getServiceInfo(SwiftServiceInfoEntity serviceInfoBean);

    List<SwiftServiceInfoEntity> getAllServiceInfo();

    List<SwiftServiceInfoEntity> getServiceInfoByService(String service);
}
