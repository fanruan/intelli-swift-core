package com.fr.swift.generate.preview.operator;

import com.fr.swift.generate.realtime.index.RealtimeSubDateColumnIndexer;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/8
 */
public class MinorSubDateColumnIndexer extends RealtimeSubDateColumnIndexer {
    private List<Segment> segments;

    public MinorSubDateColumnIndexer(DataSource dataSource, ColumnKey key, GroupType type, Segment seg) {
        super(dataSource, key, type);
        segments = Collections.singletonList(seg);
    }

    @Override
    protected List<Segment> getSegments() {
        return segments;
    }

    @Override
    protected void mergeDict() {
        new MinorSubDateColumnDictMerger(dataSource, key).work();
    }

    /**
     * @author anchore
     * @date 2018/4/8
     */
    private class MinorSubDateColumnDictMerger extends RealtimeSubDateColumnDictMerger {
        MinorSubDateColumnDictMerger(DataSource dataSource, ColumnKey key) {
            super(dataSource, key);
        }

        @Override
        protected List<Segment> getSegments() {
            return segments;
        }
    }
}