package com.fr.swift.source.etl.columnrowtrans;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/1/23 0023 14:46
 */
public class ColumnRowTransferOperator implements ETLTransferOperator {

    private String groupName;
    private String lcName;
    private List<NameText> lc_value;
    private List<NameText> columns;
    private List<String> otherColumnNames;

    public ColumnRowTransferOperator(String groupName, String lcName, List<NameText> columns,
                                           List<NameText> lc_value, List<String> otherColumnNames) {
        this.groupName = groupName;
        this.lcName = lcName;
        this.columns = columns;
        this.lc_value = lc_value;
        this.otherColumnNames = otherColumnNames;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new ColumnRowTransOperatorResultSet(this.groupName, this.lcName, this.columns, basedSegments.get(0), this.lc_value, this.otherColumnNames, metaData);
    }
}
