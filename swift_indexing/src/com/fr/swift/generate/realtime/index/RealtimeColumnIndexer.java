package com.fr.swift.generate.realtime.index;

import com.fr.general.ComparatorUtils;
import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.io.Types;
import com.fr.swift.generate.BaseColumnIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2018-1-22 10:53:18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeColumnIndexer<T> extends BaseColumnIndexer<T> {
    public RealtimeColumnIndexer(DataSource dataSource, ColumnKey key) {
        super(dataSource, key);
    }

    @Override
    protected List<Segment> getSegments() {
        List<Segment> segmentList = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        Iterator<Segment> segmentIterator = segmentList.iterator();
        while (segmentIterator.hasNext()) {
            Segment segment = segmentIterator.next();
            if (ComparatorUtils.equals(segment.getLocation().getStoreType(), Types.StoreType.FINE_IO)) {
                segmentIterator.remove();
            }
        }
        return segmentList;
    }


    @Override
    protected void mergeDict() {
        new RealtimeColumnDictMerger<T>(dataSource, key).work();
    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
        // 内存的column不释放，以后还有用
    }
}