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
    private static final SwiftContext INSTANCE = new SwiftContext();

    private SwiftContext() {
    }

    public static SwiftContext getInstance() {
        return INSTANCE;
    }

    public static void init() {
        init("com.fr.swift");
    }

    public static void init(String... packages) {
        INSTANCE.scan(packages);
        INSTANCE.refresh();
    }
}