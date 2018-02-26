package com.fr.swift.generate.realtime;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseColumnIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * This class created on 2018-1-22 10:53:18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeColumnIndexer<T extends Comparable<T>> extends BaseColumnIndexer<T> {
    public RealtimeColumnIndexer(DataSource dataSource, ColumnKey key) {
        super(dataSource, key);
    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
        // 内存的column不释放，以后还有用
    }

    protected List<Segment> getSegments() {
        return LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
    }
}
