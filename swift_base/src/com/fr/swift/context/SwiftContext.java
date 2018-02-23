package com.fr.swift.context;

import com.fr.swift.segment.SwiftSegmentProvider;

/**
 * This class created on 2018-1-30 16:58:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftContext {
    private static final SwiftContext INSTANCE = new SwiftContext();

    private SwiftContext() {
    }

    public static SwiftContext getInstance() {
        return INSTANCE;
    }

    private SwiftSegmentProvider swiftSegmentProvider;

    public void registerSwiftSegmentProvider(SwiftSegmentProvider swiftSegmentProvider) {
        this.swiftSegmentProvider = swiftSegmentProvider;
    }

    public SwiftSegmentProvider getSwiftSegmentProvider() {
        return this.swiftSegmentProvider;
    }
}