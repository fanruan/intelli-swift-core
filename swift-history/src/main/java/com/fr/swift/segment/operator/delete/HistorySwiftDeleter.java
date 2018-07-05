package com.fr.swift.segment.operator.delete;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Where;
import com.fr.swift.exception.meta.SwiftMetaDataException;
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

    public HistorySwiftDeleter(Segment segment) throws SwiftMetaDataException {
        super(segment);
    }

    @Override
    public void release() {
        segment.release();
    }

    @Override
    public ImmutableBitMap delete(SourceKey sourceKey, Where where) throws Exception {
        ImmutableBitMap allShowIndex = super.delete(sourceKey, where);
        release();
        //upload
        return allShowIndex;
    }
}
