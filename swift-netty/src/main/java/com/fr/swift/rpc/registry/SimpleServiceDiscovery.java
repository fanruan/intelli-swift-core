package com.fr.swift.rpc.registry;

import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/6/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("serviceDiscovery")
public class SimpleServiceDiscovery implements ServiceDiscovery {

    private String address;

    public SimpleServiceDiscovery() {
    }

    public SimpleServiceDiscovery(String address) {
        this.address = address;
    }

    @Override
    public String discover(String serviceName) {
        return address;
    }
}
