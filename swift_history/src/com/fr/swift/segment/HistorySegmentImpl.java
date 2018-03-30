package com.fr.swift.segment;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author anchore
 * @date 2017/12/12
 */
public class HistorySegmentImpl extends BaseSegment implements HistorySegment {
    public HistorySegmentImpl(IResourceLocation parent, SwiftMetaData meta) {
        super(parent, meta);
    }

    @Override
    public boolean isHistory() {
        return true;
    }
}