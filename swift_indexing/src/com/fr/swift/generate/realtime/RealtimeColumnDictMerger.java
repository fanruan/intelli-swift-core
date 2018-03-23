package com.fr.swift.generate.realtime;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.BaseColumnDictMerger;
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
 * @author anchore
 * @date 2018/2/26
 */
public class RealtimeColumnDictMerger<T> extends BaseColumnDictMerger<T> {
    private DataSource dataSource;

    public RealtimeColumnDictMerger(DataSource dataSource, ColumnKey key) {
        super(key);
        this.dataSource = dataSource;
    }

    @Override
    protected List<Segment> getSegments() {
        return LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
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
        // realtime的不释放
    }
}