package com.fr.swift.generate.realtime.index;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseColumnDictMerger;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * @author anchore
 * @date 2018/2/26
 */
public class RealtimeColumnDictMerger<T> extends BaseColumnDictMerger<T> {
    public RealtimeColumnDictMerger(DataSource dataSource, ColumnKey key) {
        super(dataSource, key);
    }

    @Override
    protected List<Segment> getSegments() {
        return LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
        // realtime的不释放
    }
}