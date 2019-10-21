package com.fr.swift.segment.operator.delete;

import com.fr.swift.SwiftContext;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftWhereDeleter implements WhereDeleter {

    private static final SwiftSegmentManager LOCAL_SEGS = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    private final SegmentKey segKey;

    public SwiftWhereDeleter(SegmentKey segKey) {
        this.segKey = segKey;
    }

    @Override
    public ImmutableBitMap delete(Where where) throws Exception {
        Table table = SwiftDatabase.getInstance().getTable(segKey.getTable());
        Segment seg = LOCAL_SEGS.getSegment(segKey);

        ImmutableBitMap originAllShowIndex = seg.getAllShowIndex();
        ImmutableBitMap indexAfterFilter = where.createWhereIndex(table, seg);
        ImmutableBitMap allShowIndex = originAllShowIndex.getAndNot(indexAfterFilter);

        seg.putAllShowIndex(allShowIndex);

        if (seg.isHistory()) {
            seg.release();
        }

        return allShowIndex;
    }
}
