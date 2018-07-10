package com.fr.swift.transatcion;

import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TransactionInvocationHandler implements InvocationHandler {
    private Object proxy;
    private TransactionManager transactionManager;

    TransactionInvocationHandler(Object object, TransactionManager transactionManager) {
        this.proxy = object;
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
        Method originalMethod = this.proxy.getClass().getMethod(method.getName(), method.getParameterTypes());

        Transactional transactional = originalMethod.getAnnotation(Transactional.class);

        if (transactional == null) {
            return method.invoke(this.proxy, objects);
        }
        try {
            transactionManager.start();
            Object result = method.invoke(this.proxy, objects);
            SwiftLoggers.getLogger().info("Invoke " + method + " successfully ! Do commit!");
            transactionManager.commit();
            return result;
        } catch (InvocationTargetException ite) {
            Throwable targetException = ite.getTargetException();
            rollback(method, transactional, targetException);
            throw targetException;
        } catch (Throwable e) {
            rollback(method, transactional, e);
            throw e;
        } finally {
            transactionManager.close();
        }
    }

    private void rollback(Method method, Transactional transactional, Throwable e) {
        if (transactional.value().isAssignableFrom(e.getClass())) {
            SwiftLoggers.getLogger().error("Invoke " + method + " failed ! Do rollback!");
            transactionManager.rollback();
            SwiftLoggers.getLogger().error("Rollback finished!");
        } else {
            SwiftLoggers.getLogger().error("Invoke " + method + " failed but exception is not right! Will not rollback!");
        }
    }

}
