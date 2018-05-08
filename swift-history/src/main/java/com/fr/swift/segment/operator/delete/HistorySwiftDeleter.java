package com.fr.swift.segment.operator.delete;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.Segment;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class HistorySwiftDeleter extends AbstractDeleter {

    public HistorySwiftDeleter(Segment segment) throws SwiftMetaDataException {
        super(segment);
    }

    @Override
    public void release() {
        segment.release();
    }
}
