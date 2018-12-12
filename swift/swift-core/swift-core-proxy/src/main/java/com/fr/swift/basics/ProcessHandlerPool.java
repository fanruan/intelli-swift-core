package com.fr.swift.basics;

/**
 * This class created on 2018/11/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ProcessHandlerPool {

    ProcessHandler getProcessHandler(Class<? extends ProcessHandler> aClass, InvokerCreator invokerCreator) throws Exception;
}
