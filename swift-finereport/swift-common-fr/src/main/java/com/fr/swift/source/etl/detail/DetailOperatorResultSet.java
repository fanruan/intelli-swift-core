package com.fr.swift.source.etl.detail;

import com.fr.swift.SwiftContext;
import com.fr.swift.relation.utils.RelationPathHelper;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.RelationColumn;
import com.fr.swift.segment.column.RelationColumnBuilder;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/1/15.
 */
public class DetailOperatorResultSet implements SwiftResultSet {
    private final SwiftMetaData metaData;
    private final List<ColumnKey[]> fields;
    private final List<Segment[]> segments;
    private List<RelationColumn> columns;
    private int currentRow = -1;
    private int currentTotalRow;
    private int currentSegmentIndex = 0;

    public DetailOperatorResultSet(SwiftMetaData metaData, List<ColumnKey[]> fields, List<Segment[]> segments) {
        Util.requireNonNull(fields, segments);
        this.metaData = metaData;
        this.fields = fields;
        this.segments = segments;
        moveToNextSegment();
    }

    private boolean moveToNextSegment() {
        if (currentSegmentIndex >= getBaseSegments().length) {
            return false;
        }
        columns = new ArrayList<RelationColumn>();
        for (int i = 0; i < fields.size(); i++) {
            for (ColumnKey columnKey : fields.get(i)) {
                columns.add(getRelationColumns(columnKey, i));
            }
        }
        currentTotalRow = getBaseSegments()[currentSegmentIndex].getRowCount();
        // 接着往后取 不需要从头算位置了
//        currentRow = -1;
        currentSegmentIndex++;
        return true;
    }

    private Segment[] getBaseSegments() {
        return segments.get(0);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean hasNext() {
        while (++currentRow >= currentTotalRow && moveToNextSegment()) {
        }
        return currentRow < currentTotalRow;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getNextRow() {
        List list = new ArrayList();
        for (RelationColumn column : columns) {
            Object v = null;
            try {
                v = column.getPrimaryValue(currentRow);
            } catch (Exception ignore) {
            }
            list.add(v);
        }
        return new ListBasedRow(list);
    }

    private RelationColumn getRelationColumns(ColumnKey columnKey, int foreignSegIndex) {
        Segment[] foreignSegments = segments.get(foreignSegIndex + 1);
        RelationSource relationSource = columnKey.getRelation();
        RelationIndex relationIndex;
        if (relationSource.getRelationType() == RelationSourceType.RELATION) {
            relationIndex = getBaseSegments()[currentSegmentIndex].getRelation(RelationPathHelper.convert2CubeRelation(relationSource));
        } else {
            relationIndex = getBaseSegments()[currentSegmentIndex].getRelation(RelationPathHelper.convert2CubeRelationPath(relationSource));
        }
        return SwiftContext.get().getBean(RelationColumnBuilder.class).build(relationIndex, foreignSegments, columnKey);
    }
}
