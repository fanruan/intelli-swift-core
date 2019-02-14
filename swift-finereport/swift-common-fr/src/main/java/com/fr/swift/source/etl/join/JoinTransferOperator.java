package com.fr.swift.source.etl.join;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 18:06
 */
public class JoinTransferOperator implements ETLTransferOperator {

    private List<JoinColumn> columns;
    private ColumnKey[] lKey;
    private ColumnKey[] rKey;
    private JoinType type;

    public JoinTransferOperator(List<JoinColumn> columns, ColumnKey[] lKey, ColumnKey[] rKey, JoinType type) {
        this.columns = columns;
        this.lKey = lKey;
        this.rKey = rKey;
        this.type = type;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        Segment[] lSegments = basedSegments.get(0);
        Segment[] rSegments = basedSegments.get(1);
        switch (type) {
            case OUTER:
                return new JoinOperatorResultSet(columns, lKey, metaData, rKey, lSegments, rSegments, true, true);
            case INNER:
                return new JoinOperatorResultSet(columns, lKey, metaData, rKey, lSegments, rSegments, false, false);
            case LEFT:
                return new JoinOperatorResultSet(columns, lKey, metaData, rKey, lSegments, rSegments, true, false);
            default:
                //right Join 做下反向转化
                List<JoinColumn> columns = new ArrayList<JoinColumn>();
                for (JoinColumn column : this.columns) {
                    columns.add(new JoinColumn(column.getName(), !column.isLeft(), column.getColumnName()));
                }
                return new JoinOperatorResultSet(columns, rKey, metaData, lKey, rSegments, lSegments, true, false);
        }
    }
}
