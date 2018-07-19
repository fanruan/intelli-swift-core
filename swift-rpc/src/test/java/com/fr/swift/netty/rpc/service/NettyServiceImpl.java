package com.fr.swift.netty.rpc.service;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.netty.rpc.NettyService;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RpcService(value = NettyService.class, type = RpcServiceType.SERVER_SERVICE)
public class NettyServiceImpl implements NettyService {
    @Override
    public String print(String name) {
        System.out.println(System.currentTimeMillis() + name);
        return System.currentTimeMillis() + name;
    }
}
