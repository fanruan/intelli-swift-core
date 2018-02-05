package com.fr.swift.context;

import com.fr.swift.segment.IMinorSegmentManager;
import com.fr.swift.segment.SwiftSegmentProvider;

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

    private SwiftSegmentProvider swiftSegmentProvider;

    private IMinorSegmentManager minorSegmentManager;

    public void registerSwiftSegmentProvider(SwiftSegmentProvider swiftSegmentProvider) {
        this.swiftSegmentProvider = swiftSegmentProvider;
    }

    public SwiftSegmentProvider getSwiftSegmentProvider() {
        return this.swiftSegmentProvider;
    }

    public void registerMinorSegmentManager(IMinorSegmentManager manager) {
        this.minorSegmentManager = manager;
    }

    public IMinorSegmentManager getMinorSegmentManager() {
        return this.minorSegmentManager;
    }
}