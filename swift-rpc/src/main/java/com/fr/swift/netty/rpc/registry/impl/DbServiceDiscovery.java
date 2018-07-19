package com.fr.swift.netty.rpc.registry.impl;

import com.fr.swift.config.bean.RpcServiceAddressBean;
import com.fr.swift.config.service.SwiftServiceAddressService;
import com.fr.swift.netty.rpc.registry.ServiceDiscovery;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/15
 */
@Service("dbServiceDiscovery")
public class DbServiceDiscovery implements ServiceDiscovery {

    @Autowired(required = false)
    private SwiftServiceAddressService config;
    @Override
    public String discover(String serviceName) {
        RpcServiceAddressBean address = config.getAddress(serviceName);
        if (null == address) {
            return address.getFullAddress();
        }
        return null;
    }
}
