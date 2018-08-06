package com.fr.swift.netty.rpc.server;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.netty.rpc.SwiftRpcService;
import com.fr.workspace.simple.SimpleWork;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServerStart {

    public static void main(String[] args) {
        SimpleWork.checkIn(System.getProperty("user.dir"));
        SwiftContext.init();
        SwiftRpcService swiftRpcService = SwiftRpcService.getInstance();
        swiftRpcService.startServerService();
    }
}
