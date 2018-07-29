package com.fr.swift.segment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Where;
import com.fr.swift.segment.operator.delete.RealtimeSwiftDeleter;
import com.fr.swift.segment.operator.delete.SwiftWhereDeleter;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class Decrementer implements WhereDeleter {
    private SourceKey tableKey;

    private Segment segment;

    public Decrementer(SourceKey tableKey, Segment segment) {
        this.tableKey = tableKey;
        this.segment = segment;
    }

    @Override
    public ImmutableBitMap delete(Where where) throws Exception {
        WhereDeleter whereDeleter;
        if (segment.isHistory()) {
            whereDeleter = new SwiftWhereDeleter(tableKey, segment);
        } else {
            whereDeleter = new RealtimeSwiftDeleter(tableKey, segment);
        }
        return whereDeleter.delete(where);
    }
}
