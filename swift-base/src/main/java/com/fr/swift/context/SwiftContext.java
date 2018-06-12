package com.fr.swift.context;

import com.fr.third.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * This class created on 2018-1-30 16:58:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftContext extends AnnotationConfigApplicationContext {
    public static void init() {
        init("com.fr.swift");
    }

    public static void init(String... packages) {
        if (INSTANCE.refreshed) {
            return;
        }
        synchronized (INSTANCE) {
            if (INSTANCE.refreshed) {
                return;
            }
            INSTANCE.scan(packages);
            INSTANCE.refresh();

            INSTANCE.refreshed = true;
        }
    }

    private static final SwiftContext INSTANCE = new SwiftContext();

    private boolean refreshed = false;

    private SwiftContext() {
    }

    public static SwiftContext getInstance() {
        return INSTANCE;
    }
}