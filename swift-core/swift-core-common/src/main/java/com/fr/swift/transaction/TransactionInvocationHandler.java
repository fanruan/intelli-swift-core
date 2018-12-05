package com.fr.swift.transaction;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.MonitorUtil;

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
        MonitorUtil.start();
        if (transactional == null) {
            return method.invoke(this.proxy, objects);
        }
        try {
            transactionManager.start();
            Object result = method.invoke(this.proxy, objects);
            SwiftLoggers.getLogger().debug(String.format("Invoke %s.%s successfully ! Do commit!", method.getDeclaringClass().getSimpleName(), method.getName()));
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
            MonitorUtil.finish(method.getName());
        }
    }

    private void rollback(Method method, Transactional transactional, Throwable e) {
        SwiftLoggers.getLogger().error(
                String.format("Invoke %s.%s failed ! Do rollback!", method.getDeclaringClass().getSimpleName(), method.getName()));
        transactionManager.rollback();
        SwiftLoggers.getLogger().error("Rollback finished!");
    }

}
