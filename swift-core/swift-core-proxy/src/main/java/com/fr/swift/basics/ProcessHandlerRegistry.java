package com.fr.swift.basics;

/**
 * This class created on 2018/11/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ProcessHandlerRegistry {
    /**
     * 注册ProcessHandler
     *
     * @param iProcessHandler
     * @param cProcessHandler
     */
    void addHandler(Class<? extends ProcessHandler> iProcessHandler, Class<? extends ProcessHandler> cProcessHandler);

    /**
     * 获取ProcessHandler
     *
     * @param iProcessHandler
     * @return
     */
    Class getHandler(Class<? extends ProcessHandler> iProcessHandler);
}
