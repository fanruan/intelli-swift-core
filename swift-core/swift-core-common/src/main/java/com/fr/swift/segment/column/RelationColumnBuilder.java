package com.fr.swift.segment.column;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.relation.RelationIndex;

import java.util.List;

/**
 * @author yee
 * @date 2018/9/7
 */
public interface RelationColumnBuilder {
    RelationColumn build(RelationIndex relationIndex, Segment[] segments, ColumnKey columnKey);

    RelationColumn build(RelationIndex relationIndex, List<Segment> segments, ColumnKey columnKey);

    RelationColumn build(ColumnKey columnKey);
}
