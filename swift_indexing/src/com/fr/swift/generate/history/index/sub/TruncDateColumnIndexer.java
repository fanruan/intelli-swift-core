package com.fr.swift.generate.history.index.sub;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseSubDateColumnIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.TruncDateColumn;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * @author anchore
 * @date 2018/3/24
 */
public class TruncDateColumnIndexer extends BaseSubDateColumnIndexer<Long> {
    public TruncDateColumnIndexer(DataSource dataSource, ColumnKey key, GroupType type) {
        super(dataSource, key, type);
    }

    @Override
    protected List<Segment> getSegments() {
        return LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
    }

    @Override
    protected Column<Long> transform(Column<Long> origin) {
        return new TruncDateColumn(origin, type);
    }

    @Override
    protected void mergeDict() {

    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
        baseColumn.release();
    }
}