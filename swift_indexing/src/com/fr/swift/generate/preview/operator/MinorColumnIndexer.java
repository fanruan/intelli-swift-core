package com.fr.swift.generate.preview.operator;

import com.fr.swift.generate.realtime.index.RealtimeColumnIndexer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/8
 */
public class MinorColumnIndexer extends RealtimeColumnIndexer {
    private List<Segment> segments;

    public MinorColumnIndexer(DataSource dataSource, ColumnKey key, Segment seg) {
        super(dataSource, key);
        segments = Collections.singletonList(seg);
    }

    @Override
    protected List<Segment> getSegments() {
        return segments;
    }

    @Override
    protected void mergeDict() {
        new MinorColumnDictMerger(dataSource, key).work();
    }

    /**
     * @author anchore
     * @date 2018/4/8
     */
    private class MinorColumnDictMerger extends RealtimeColumnDictMerger {
        MinorColumnDictMerger(DataSource dataSource, ColumnKey key) {
            super(dataSource, key);
        }

        @Override
        protected List<Segment> getSegments() {
            return segments;
        }
    }
}