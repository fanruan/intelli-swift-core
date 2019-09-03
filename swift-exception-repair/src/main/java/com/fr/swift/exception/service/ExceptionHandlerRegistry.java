package com.fr.swift.exception.service;

import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.handler.ExceptionHandler;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author Marvin
 * @date 8/16/2019
 * @description
 * @since swift 1.1
 */
public class ExceptionHandlerRegistry {

    private static final ExceptionHandlerRegistry INSTANCE = new ExceptionHandlerRegistry();

    public static ExceptionHandlerRegistry getInstance() {
        return INSTANCE;
    }

    private Map<ExceptionInfo.Type, ExceptionHandler> handlerMap = new IdentityHashMap<>();

    private ExceptionHandlerRegistry() {
    }

    public void registerExceptionHandler(ExceptionHandler handler) {
        handlerMap.put(handler.getExceptionInfoType(), handler);
    }

    public ExceptionHandler getHandler(ExceptionInfo.Type infoType) {
        return handlerMap.get(infoType);
    }
}
