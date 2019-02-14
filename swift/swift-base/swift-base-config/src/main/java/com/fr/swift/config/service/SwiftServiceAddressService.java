package com.fr.swift.config.service;

import com.fr.swift.config.bean.RpcServiceAddressBean;

/**
 * @author yee
 * @date 2018/6/27
 */
@Deprecated
public interface SwiftServiceAddressService {
    boolean addOrUpdateAddress(String serviceName, RpcServiceAddressBean address);

    RpcServiceAddressBean getAddress(String serviceName);
}
