package com.fr.swift.netty.rpc.server;

import com.fr.swift.SwiftContext;
import com.fr.swift.netty.rpc.service.SwiftRpcService;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServerStart {

    public static void main(String[] args) {
//        SimpleWork.checkIn(System.getProperty("user.dir"));
        SwiftContext.get().init();
        SwiftRpcService swiftRpcService = SwiftRpcService.getInstance();
        swiftRpcService.startServerService();
    }
}
