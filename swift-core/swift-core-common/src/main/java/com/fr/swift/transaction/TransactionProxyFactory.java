package com.fr.swift.transaction;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TransactionProxyFactory {

    private TransactionManager transactionManager;

    public TransactionProxyFactory(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object getProxy(Object object) {

        List<Class> interfaces = new ArrayList<Class>();
        Class clazz = object.getClass();
        while (clazz != null) {
            interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            clazz = clazz.getSuperclass();
        }
        return Proxy.newProxyInstance(object.getClass().getClassLoader(),
                interfaces.toArray(new Class[interfaces.size()]), new TransactionInvocationHandler(object, transactionManager));
    }
}
