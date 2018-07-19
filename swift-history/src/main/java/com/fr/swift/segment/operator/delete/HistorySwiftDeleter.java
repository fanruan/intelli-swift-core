package com.fr.swift.segment.operator.delete;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class HistorySwiftDeleter extends AbstractDeleter {
    public HistorySwiftDeleter(SourceKey tableKey, Segment segment) {
        super(tableKey, segment);
    }

    @Override
    public void release() {
        segment.release();
    }
}
