package com.fr.swift.netty.rpc.registry.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.RpcServiceAddressBean;
import com.fr.swift.config.service.SwiftServiceAddressService;
import com.fr.swift.netty.rpc.registry.ServiceDiscovery;

/**
 * @author yee
 * @date 2018/6/15
 */
@SwiftBean(name = "dbServiceDiscovery")
public class DbServiceDiscovery implements ServiceDiscovery {

    private SwiftServiceAddressService config = SwiftContext.get().getBean(SwiftServiceAddressService.class);

    @Override
    public String discover(String serviceName) {
        RpcServiceAddressBean address = config.getAddress(serviceName);
        if (null == address) {
            return address.getFullAddress();
        }
        return null;
    }
}
