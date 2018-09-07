package com.fr.swift.relation.column;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.RelationColumn;
import com.fr.swift.segment.column.RelationColumnBuilder;
import com.fr.swift.segment.relation.RelationIndex;

import java.util.List;

/**
 * @author yee
 * @date 2018/9/7
 */
public class RelationColumnBuilderImpl implements RelationColumnBuilder {
    @Override
    public RelationColumn build(RelationIndex relationIndex, Segment[] segments, ColumnKey columnKey) {
        return new RelationColumnImpl(relationIndex, segments, columnKey);
    }

    @Override
    public RelationColumn build(RelationIndex relationIndex, List<Segment> segments, ColumnKey columnKey) {
        return new RelationColumnImpl(relationIndex, segments, columnKey);
    }

    @Override
    public RelationColumn build(ColumnKey columnKey) {
        return new RelationColumnImpl(columnKey);
    }
}
