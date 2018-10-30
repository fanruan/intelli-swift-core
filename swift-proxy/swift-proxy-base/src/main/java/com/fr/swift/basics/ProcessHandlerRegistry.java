package com.fr.swift.basics;

/**
 * @author yee
 * @date 2018/10/24
 */
public interface ProcessHandlerRegistry {
    /**
     * 注册ProcessHandler
     * @param cProcessHandler
     * @param processHandler
     */
    void addHandler(Class<? extends ProcessHandler> cProcessHandler, ProcessHandler processHandler);

    /**
     * 获取ProcessHandler
     * @param cProcessHandler
     * @return
     */
    ProcessHandler getHandler(Class<? extends ProcessHandler> cProcessHandler);
}
