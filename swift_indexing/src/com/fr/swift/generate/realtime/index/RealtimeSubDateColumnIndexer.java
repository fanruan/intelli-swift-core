package com.fr.swift.generate.realtime.index;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseColumnDictMerger;
import com.fr.swift.generate.BaseColumnIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.DateColumn;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * @author anchore
 * @date 2018/3/23
 */
public class RealtimeSubDateColumnIndexer<Derive> extends BaseColumnIndexer<Derive> {
    protected GroupType type;

    public RealtimeSubDateColumnIndexer(DataSource dataSource, ColumnKey key, GroupType type) {
        super(dataSource, key);
        this.type = type;
    }

    @Override
    protected List<Segment> getSegments() {
        return LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
    }

    @Override
    protected Column<Derive> getColumn(Segment segment) {
        return (Column<Derive>) new SubDateColumn((DateColumn) super.getColumn(segment), type);
    }

    @Override
    protected void mergeDict() {
        new RealtimeSubDateColumnDictMerger(dataSource, key).work();
    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
    }

    /**
     * @author anchore
     * @date 2018/1/9
     * <p>
     * 合并字典
     */
    public class RealtimeSubDateColumnDictMerger extends BaseColumnDictMerger<Derive> {
        public RealtimeSubDateColumnDictMerger(DataSource dataSource, ColumnKey key) {
            super(dataSource, key);
        }

        @Override
        protected List<Segment> getSegments() {
            return RealtimeSubDateColumnIndexer.this.getSegments();
        }

        @Override
        protected Column<Derive> getColumn(Segment segment) {
            return RealtimeSubDateColumnIndexer.this.getColumn(segment);
        }

        @Override
        protected void releaseIfNeed(Releasable baseColumn) {
        }
    }
}