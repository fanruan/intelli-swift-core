package com.fr.swift.rpc.registry;

import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/6/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("serviceRegistry")
public class SimpleServiceRegistry implements ServiceRegistry {

    @Override
    public void register(String serviceName, String serviceAddress) {

    }
}
