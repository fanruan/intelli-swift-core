package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Handsome on 2018/1/22 0022 10:14
 */
public class OneUnionRelationTransferOperator implements ETLTransferOperator {
    private LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>();
    private String idColumnName;
    private List<String> showColumns = new ArrayList<String>();
    private int columnType;
    private String columnName;

    public OneUnionRelationTransferOperator(String columnName, List<String> showColumns, String idColumnName,
                                            int columnType, LinkedHashMap<String, Integer> columns) {
        this.columnName = columnName;
        this.showColumns = showColumns;
        this.idColumnName = idColumnName;
        this.columnType = columnType;
        this.columns = columns;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new OneUnionRelationOperatorResultSet(this.columnName, this.showColumns, this.idColumnName, this.columnType, this.columns, basedSegments.get(0), metaData);
    }
}
