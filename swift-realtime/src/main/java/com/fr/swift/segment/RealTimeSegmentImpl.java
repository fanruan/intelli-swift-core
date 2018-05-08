package com.fr.swift.segment;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.source.SwiftMetaData;

/**
 * This class created on 2018-1-9 11:06:45
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealTimeSegmentImpl extends BaseSegment implements RealTimeSegment {
    public RealTimeSegmentImpl(IResourceLocation parent, SwiftMetaData meta) {
        super(parent, meta);
    }

    @Override
    public boolean isHistory() {
        return false;
    }
}