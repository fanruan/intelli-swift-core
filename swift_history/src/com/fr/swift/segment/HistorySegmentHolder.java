package com.fr.swift.segment;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.AbstractSegmentHolder;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018/1/4
 */
public class HistorySegmentHolder extends AbstractSegmentHolder {

    public HistorySegmentHolder(SwiftMetaData metaData, Segment segment) throws SwiftMetaDataException {
        super(metaData, segment);
    }
}

