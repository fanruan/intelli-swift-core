package com.fr.swift.rpc.registry.impl;

import com.fr.swift.config.SwiftServiceAddressConfig;
import com.fr.swift.config.bean.RpcServiceAddress;
import com.fr.swift.rpc.registry.ServiceRegistry;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/15
 */
@Service("dbServiceRegistry")
public class DbServiceRegistry implements ServiceRegistry {
    private SwiftServiceAddressConfig config = SwiftServiceAddressConfig.getInstance();

    @Override
    public void register(String serviceName, String serviceAddress) {
        config.addOrUpdateAddress(serviceAddress, new RpcServiceAddress(serviceAddress));
    }
}
