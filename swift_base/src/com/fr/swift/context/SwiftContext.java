package com.fr.swift.context;

import com.fr.swift.segment.SegmentOperatorProvider;
import com.fr.swift.segment.SwiftSegmentManager;

/**
 * This class created on 2018-1-30 16:58:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftContext {
    private static SwiftContext INSTANCE = new SwiftContext();

    private SwiftContext() {
    }

    public static SwiftContext getInstance() {
        return INSTANCE;
    }

    private SwiftSegmentManager segmentProvider;

    private SegmentOperatorProvider segmentOperatorProvider;

    private SwiftSegmentManager minorSegmentManager;

    public void registerSegmentProvider(SwiftSegmentManager segmentProvider) {
        this.segmentProvider = segmentProvider;
    }

    public void registerSegmentOperatorProvider(SegmentOperatorProvider segmentOperatorProvider) {
        this.segmentOperatorProvider = segmentOperatorProvider;
    }

    public SwiftSegmentManager getSegmentProvider() {
        return this.segmentProvider;
    }

    public SegmentOperatorProvider getSegmentOperatorProvider() {
        return this.segmentOperatorProvider;
    }

    public void registerMinorSegmentManager(SwiftSegmentManager manager) {
        this.minorSegmentManager = manager;
    }

    public SwiftSegmentManager getMinorSegmentManager() {
        return minorSegmentManager;
    }
}