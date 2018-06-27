package com.fr.swift.config.service.impl;

import com.fr.swift.config.SwiftServiceAddressConfig;
import com.fr.swift.config.bean.RpcServiceAddress;
import com.fr.swift.config.service.SwiftServiceAddressService;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/27
 */
@Service("swiftServiceAddressService")
public class SwiftServiceAddressServiceImpl implements SwiftServiceAddressService {
    private SwiftServiceAddressConfig config = SwiftServiceAddressConfig.getInstance();

    @Override
    public boolean addOrUpdateAddress(String serviceName, RpcServiceAddress address) {
        return config.addOrUpdateAddress(serviceName, address);
    }

    @Override
    public RpcServiceAddress getAddress(String serviceName) {
        return config.getAddressByServiceName(serviceName);
    }
}
