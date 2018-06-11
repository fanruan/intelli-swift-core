package com.fr.swift.rpc.service;

import com.fr.swift.rpc.NettyService;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.rpc.annotation.RpcServiceType;

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
