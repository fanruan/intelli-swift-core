package com.fr.swift.config.hibernate.transaction;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.hibernate.HibernateManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;

/**
 * @author yee
 * @date 2018/6/30
 */
@SwiftBean
public class HibernateTransactionManager extends BaseTransactionManager {

    private HibernateManager hibernateManager = SwiftContext.get().getBean(HibernateManager.class);

    @Override
    protected Object createSession() {
        return hibernateManager.getFactory().openSession();
    }
}
