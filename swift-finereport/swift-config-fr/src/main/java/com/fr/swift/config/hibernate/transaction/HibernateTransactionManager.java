package com.fr.swift.config.hibernate.transaction;

import com.fr.swift.config.hibernate.HibernateManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/30
 */
@Service
public class HibernateTransactionManager extends BaseTransactionManager {

    @Autowired
    private HibernateManager hibernateManager;

    @Override
    protected Object createSession() {
        return hibernateManager.getFactory().openSession();
    }
}
