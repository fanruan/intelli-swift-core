package com.fr.swift.context;

import com.fr.third.springframework.beans.factory.BeanFactory;
import com.fr.third.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * This class created on 2018-1-30 16:58:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftContext extends AnnotationConfigApplicationContext {
    private static final SwiftContext INSTANCE = new SwiftContext();

    private SwiftContext() {
    }

    public static BeanFactory getInstance() {
        return INSTANCE;
    }

    public static void init() {
        INSTANCE.scan("com.fr.swift");
        INSTANCE.refresh();
    }
}