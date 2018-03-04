package com.fr.swift.source.etl.join;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;
import com.fr.swift.source.etl.utils.ETLConstant;

import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 18:06
 */
public class JoinTransferOperator implements ETLTransferOperator {

    private List<JoinColumn> columns;
    private ColumnKey[] lKey;
    private ColumnKey[] rKey;
    private int type;

    public JoinTransferOperator(List<JoinColumn> columns, ColumnKey[] lKey, ColumnKey[] rKey, int type) {
        this.columns = columns;
        this.lKey = lKey;
        this.rKey = rKey;
        this.type = type;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        Segment[] lSegments = basedSegments.get(0);
        Segment[] rSegments = basedSegments.get(1);
        if (this.type == ETLConstant.JOINTYPE.OUTER) {
            return new JoinOperatorResultSet(columns, lKey, metaData, rKey, lSegments, rSegments, false, true);
        } else if (this.type == ETLConstant.JOINTYPE.INNER) {
            return new JoinOperatorResultSet(columns, lKey, metaData, rKey, lSegments, rSegments, true, false);
        } else if (this.type == ETLConstant.JOINTYPE.LEFT) {
            return new JoinOperatorResultSet(columns, lKey, metaData, rKey, lSegments, rSegments, false, false);
        } else {
            return new JoinOperatorResultSet(columns, lKey, metaData, rKey, lSegments, rSegments, true, true);
        }

    }
}
