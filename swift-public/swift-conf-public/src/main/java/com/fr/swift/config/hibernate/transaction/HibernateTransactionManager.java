package com.fr.swift.config.hibernate.transaction;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.hibernate.HibernateManager;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.impl.BaseTransactionManager;
import com.fr.swift.config.oper.proxy.SessionInvocationHandler;
import org.hibernate.Session;

import java.lang.reflect.Proxy;

/**
 * @author yee
 * @date 2018-11-28
 */
@SwiftBean
public class HibernateTransactionManager extends BaseTransactionManager {

    @Override
    protected ConfigSession createSession() {
        Session session = HibernateManager.INSTANCE.getFactory().openSession();
        return (ConfigSession) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{ConfigSession.class}, new SessionInvocationHandler(session));
    }
}
