package com.fr.swift.config.hibernate.transaction;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.hibernate.HibernateManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;

/**
 * @author yee
 * @date 2018-11-28
 */
@SwiftBean
public class HibernateTransactionManager extends BaseTransactionManager {

    @Override
    protected Object createSession() {
        return HibernateManager.INSTANCE.getFactory().openSession();
    }
}
