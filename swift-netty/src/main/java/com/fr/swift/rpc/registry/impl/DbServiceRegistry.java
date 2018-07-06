package com.fr.swift.rpc.registry.impl;

import com.fr.swift.config.bean.unique.RpcServiceAddressUnique;
import com.fr.swift.config.service.SwiftServiceAddressService;
import com.fr.swift.rpc.registry.ServiceRegistry;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/15
 */
@Service("dbServiceRegistry")
public class DbServiceRegistry implements ServiceRegistry {
    @Autowired(required = false)
    private SwiftServiceAddressService config;

    @Override
    public void register(String serviceName, String serviceAddress) {
        config.addOrUpdateAddress(serviceAddress, new RpcServiceAddressUnique(serviceAddress).convert());
    }
}
