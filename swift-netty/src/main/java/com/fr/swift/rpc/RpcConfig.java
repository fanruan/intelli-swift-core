package com.fr.swift.rpc;

import com.fr.third.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import com.fr.third.springframework.core.io.ClassPathResource;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author anchore
 * @date 2018/6/12
 */
@Service
public class RpcConfig extends PropertyPlaceholderConfigurer {
    public RpcConfig() {
        setLocation(new ClassPathResource("rpc.properties"));
    }
}