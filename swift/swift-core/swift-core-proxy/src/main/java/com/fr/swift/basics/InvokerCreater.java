package com.fr.swift.basics;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface InvokerCreater {

    Invoker createAsyncInvoker(Class clazz, URL url);

    Invoker createSyncInvoker(Class clazz, URL url);

    InvokerType getType();
}
