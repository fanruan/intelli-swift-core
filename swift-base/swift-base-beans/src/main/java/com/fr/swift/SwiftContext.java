package com.fr.swift;

import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.beans.factory.SwiftBeanFactory;

/**
 * This class created on 2018-1-30 16:58:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftContext extends SwiftBeanFactory {
    private static final BeanFactory INSTANCE = new SwiftContext();

    private boolean refreshed = false;

    private SwiftContext() {
//        registerPackages("com.fr.swift");
//        registerPackages(
//                // RPC和HTTP服务
//                "com.fr.swift.netty.rpc.service",
//                // 各种Service
//                "com.fr.swift.service.listener",
//                "com.fr.swift.service",
//                "com.fr.swift.cluster.service",
//                "com.fr.swift.config",
//                "com.fr.swift.property",
//                // RM NM节点
//                "com.fr.swift.rm",
//                "com.fr.swift.nm",
//                // 共享存储
//                "com.fr.swift.file",
//                "com.fr.swift.api");
    }

    @Override
    public void init() {
        if (refreshed) {
            return;
        }
        synchronized (INSTANCE) {
            if (refreshed) {
                return;
            }
            super.init();
            refreshed = true;
        }
    }

    public static BeanFactory get() {
        return INSTANCE;
    }
}