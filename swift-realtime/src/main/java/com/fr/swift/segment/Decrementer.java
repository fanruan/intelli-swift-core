package com.fr.swift.segment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Where;
import com.fr.swift.segment.operator.delete.HistorySwiftDeleter;
import com.fr.swift.segment.operator.delete.RealtimeSwiftDeleter;
import com.fr.swift.segment.operator.delete.RowDeleter;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.Crasher;

import java.util.List;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class Decrementer implements RowDeleter {

    private static final SwiftSegmentManager LOCAL_SEGMENT_PROVIDER = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    private Segment segment;

    public Decrementer(Segment segment) {
        this.segment = segment;
    }

    @Override
    public boolean deleteData(List<Row> rowList) {
        Crasher.crash("method not supported");
        return false;
    }

    @Override
    public boolean deleteData(SwiftResultSet swiftResultSet) {
        Crasher.crash("method not supported");
        return false;
    }

    @Override
    public ImmutableBitMap delete(SourceKey sourceKey, Where where) throws Exception {
        RowDeleter rowDeleter;
        if (segment.isHistory()) {
            rowDeleter = new HistorySwiftDeleter(segment);
        } else {
            rowDeleter = new RealtimeSwiftDeleter(segment);
        }
        return rowDeleter.delete(sourceKey, where);
    }
}
