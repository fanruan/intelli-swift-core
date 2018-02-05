package com.fr.swift.segment;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.AbstractSegmentHolder;
import com.fr.swift.segment.Segment;

/**
 * @author yee
 * @date 2018/1/4
 */
public class HistorySegmentHolder extends AbstractSegmentHolder {

    public HistorySegmentHolder(Segment segment) throws SwiftMetaDataException {
        super(segment);
    }
}

