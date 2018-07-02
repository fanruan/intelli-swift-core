package com.fr.swift.transatcion;

import com.fr.swift.log.SwiftLogger;
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

    private SwiftLogger LOGGER = SwiftLoggers.getLogger(TransactionInvocationHandler.class);

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
            LOGGER.info("Invoke successfully ! Do commit!");
            transactionManager.commit();
            return result;
        } catch (InvocationTargetException ite) {
            Throwable targetException = ite.getTargetException();
            if (transactional.value().isAssignableFrom(targetException.getClass())) {
                LOGGER.error("Invoke failed ! Do rollback!");
                transactionManager.rollback();
                LOGGER.error("Rollback finished!");
            } else {
                LOGGER.error("Invoke failed but exception is not right! Will not rollback!");
            }
            throw targetException;
        } catch (Throwable e) {
            if (transactional.value().isAssignableFrom(e.getClass())) {
                LOGGER.error("Invoke failed ! Do rollback!");
                transactionManager.rollback();
                LOGGER.error("Rollback finished!");
            } else {
                LOGGER.error("Invoke failed but exception is not right! Will not rollback!");
            }
            throw e;
        } finally {
            transactionManager.close();
        }
    }

}
