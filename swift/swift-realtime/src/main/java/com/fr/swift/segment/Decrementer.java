package com.fr.swift.segment;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Where;
import com.fr.swift.segment.operator.delete.RealtimeSwiftDeleter;
import com.fr.swift.segment.operator.delete.SwiftWhereDeleter;
import com.fr.swift.segment.operator.delete.WhereDeleter;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "decrementer")
@SwiftScope("prototype")
public class Decrementer implements WhereDeleter {
    private SegmentKey segKey;

    public Decrementer(SegmentKey segKey) {
        this.segKey = segKey;
    }

    @Override
    public ImmutableBitMap delete(Where where) throws Exception {
        WhereDeleter whereDeleter;
        if (segKey.getStoreType().isTransient()) {
            whereDeleter = new RealtimeSwiftDeleter(segKey);
        } else {
            whereDeleter = new SwiftWhereDeleter(segKey);
        }
        return whereDeleter.delete(where);
    }
}
