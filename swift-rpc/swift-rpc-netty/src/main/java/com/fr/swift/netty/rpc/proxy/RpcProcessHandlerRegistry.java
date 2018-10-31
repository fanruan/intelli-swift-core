package com.fr.swift.netty.rpc.proxy;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.base.BaseProcessHandlerRegistry;

/**
 * @author yee
 * @date 2018/10/25
 */
public class RpcProcessHandlerRegistry extends BaseProcessHandlerRegistry {
    /**
     * TODO 注册handler
     *
     * @return
     */
    @Override
    protected ProcessHandler[] registerProcessHandlers() {
        return new ProcessHandler[]{
                new RpcMasterProcessHandler()
        };
    }
}
