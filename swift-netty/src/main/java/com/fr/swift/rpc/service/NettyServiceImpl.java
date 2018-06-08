package com.fr.swift.rpc.service;

import com.fr.swift.rpc.NettyService;
import com.fr.swift.rpc.annotation.RpcService;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RpcService(NettyService.class)
public class NettyServiceImpl implements NettyService {
    @Override
    public String print(String name) {
        System.out.println(System.currentTimeMillis() + name);
        return System.currentTimeMillis() + name;
    }
}
