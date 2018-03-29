package com.fr.swift.generate.history.index;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseColumnDictMerger;
import com.fr.swift.generate.BaseColumnIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * This class created on 2017-12-28 10:54:47
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ColumnIndexer<T> extends BaseColumnIndexer<T> {
    public ColumnIndexer(DataSource dataSource, ColumnKey key) {
        super(dataSource, key);
    }

    @Override
    protected List<Segment> getSegments() {
        return LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
    }

    @Override
    protected void mergeDict() {
        new ColumnDictMerger(dataSource, key).work();
    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
        baseColumn.release();
    }

    /**
     * @author anchore
     * @date 2018/1/9
     * <p>
     * 合并字典
     */
    public class ColumnDictMerger extends BaseColumnDictMerger<T> {
        public ColumnDictMerger(DataSource dataSource, ColumnKey key) {
            super(dataSource, key);
        }

        @Override
        protected List<Segment> getSegments() {
            return ColumnIndexer.this.getSegments();
        }

        @Override
        protected void releaseIfNeed(Releasable baseColumn) {
            baseColumn.release();
        }
    }
}