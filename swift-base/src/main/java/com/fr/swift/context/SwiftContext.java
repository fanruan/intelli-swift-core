package com.fr.swift.context;

import com.fr.third.springframework.context.ApplicationContext;
import com.fr.third.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class created on 2018-1-30 16:58:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftContext extends ClassPathXmlApplicationContext {
    public static void init() {
        if (INSTANCE.refreshed) {
            return;
        }
        synchronized (INSTANCE) {
            if (INSTANCE.refreshed) {
                return;
            }
            INSTANCE.setConfigLocation("swift-context.xml");
            INSTANCE.refresh();

            INSTANCE.refreshed = true;
        }
    }

    private static final SwiftContext INSTANCE = new SwiftContext();

    private boolean refreshed = false;

    private SwiftContext() {
    }

    public static ApplicationContext getInstance() {
        return INSTANCE;
    }
}