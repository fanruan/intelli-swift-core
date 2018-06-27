package com.fr.swift.config.service;

import com.fr.swift.config.bean.RpcServiceAddress;

/**
 * @author yee
 * @date 2018/6/27
 */
public interface SwiftServiceAddressService {
    boolean addOrUpdateAddress(String serviceName, RpcServiceAddress address);

    RpcServiceAddress getAddress(String serviceName);
}
