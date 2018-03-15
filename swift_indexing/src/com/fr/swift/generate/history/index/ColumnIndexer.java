package com.fr.swift.generate.history.index;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.BaseColumnIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Crasher;

import java.util.List;

/**
 * This class created on 2017-12-28 10:54:47
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ColumnIndexer<T extends Comparable<T>> extends BaseColumnIndexer<T> {
    private DataSource dataSource;

    public ColumnIndexer(DataSource dataSource, ColumnKey key) {
        super(key);
        this.dataSource = dataSource;
    }

    @Override
    protected List<Segment> getSegments() {
        return LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
    }

    @Override
    protected void mergeDict() {
        new ColumnDictMerger<T>(dataSource, key).work();
    }

    @Override
    protected ClassType getClassType() {
        try {
            SwiftMetaDataColumn metaColumn = dataSource.getMetadata().getColumn(key.getName());
            return ColumnTypeUtils.sqlTypeToClassType(
                    metaColumn.getType(),
                    metaColumn.getPrecision(),
                    metaColumn.getScale()
            );
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
        baseColumn.release();
    }
}