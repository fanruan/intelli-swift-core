package com.fr.swift.netty.rpc.registry.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.RpcServiceAddressBean;
import com.fr.swift.config.service.SwiftServiceAddressService;
import com.fr.swift.netty.rpc.registry.ServiceRegistry;

/**
 * @author yee
 * @date 2018/6/15
 */
@SwiftBean(name = "dbServiceRegistry")
public class DbServiceRegistry implements ServiceRegistry {
    private SwiftServiceAddressService config = SwiftContext.get().getBean(SwiftServiceAddressService.class);

    @Override
    public void register(String serviceName, String serviceAddress) {
        config.addOrUpdateAddress(serviceAddress, new RpcServiceAddressBean(serviceAddress));
    }
}
