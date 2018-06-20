package com.fr.swift.rpc.registry.impl;

import com.fr.swift.config.SwiftServiceAddressConfig;
import com.fr.swift.config.bean.RpcServiceAddress;
import com.fr.swift.rpc.registry.ServiceDiscovery;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/15
 */
@Service("dbServiceDiscovery")
public class DbServiceDiscovery implements ServiceDiscovery {
    private SwiftServiceAddressConfig config = SwiftServiceAddressConfig.getInstance();

    @Override
    public String discover(String serviceName) {
        RpcServiceAddress address = config.getAddressByServiceName(serviceName);
        if (null == address) {
            return address.getFullAddress();
        }
        return null;
    }
}
