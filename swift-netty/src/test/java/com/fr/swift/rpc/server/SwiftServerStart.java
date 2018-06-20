package com.fr.swift.rpc.server;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.rpc.SwiftRpcService;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServerStart {

    public static void main(String[] args) {
        SwiftContext.init();
        SwiftRpcService swiftRpcService = SwiftRpcService.getInstance();
        swiftRpcService.startServerService();
    }
}
