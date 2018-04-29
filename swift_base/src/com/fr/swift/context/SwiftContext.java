package com.fr.swift.context;

import com.fr.swift.segment.SwiftDataOperatorProvider;
import com.fr.swift.segment.SwiftSegmentManager;

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

    private SwiftSegmentManager segmentProvider;

    private SwiftDataOperatorProvider swiftDataOperatorProvider;

    private SwiftSegmentManager minorSegmentManager;

    public String getLocalCubePath() {
        return System.getProperty("user.dir") + "/cubes";
    }

    public void registerSegmentProvider(SwiftSegmentManager segmentProvider) {
        this.segmentProvider = segmentProvider;
    }

    public void registerSegmentOperatorProvider(SwiftDataOperatorProvider swiftDataOperatorProvider) {
        this.swiftDataOperatorProvider = swiftDataOperatorProvider;
    }

    public SwiftSegmentManager getSegmentProvider() {
        return this.segmentProvider;
    }

    public SwiftDataOperatorProvider getSwiftDataOperatorProvider() {
        return this.swiftDataOperatorProvider;
    }
}