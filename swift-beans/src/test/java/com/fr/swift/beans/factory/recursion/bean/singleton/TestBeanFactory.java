package com.fr.swift.beans.factory.recursion.bean.singleton;

import com.fr.swift.beans.factory.SwiftBeanFactory;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
class TestBeanFactory {
    private static final SwiftBeanFactory INSTANCE = new SwiftBeanFactory();

    protected static SwiftBeanFactory getInstance() {
        return INSTANCE;
    }
}
