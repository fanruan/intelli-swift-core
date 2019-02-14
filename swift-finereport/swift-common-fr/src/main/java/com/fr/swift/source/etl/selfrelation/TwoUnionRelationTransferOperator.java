package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Handsome on 2018/1/19 0019 10:33
 */
public class TwoUnionRelationTransferOperator implements ETLTransferOperator {

    private LinkedHashMap<String, Integer> columns;
    private String idColumnName;
    private List<String> showColumns;
    private String[] addedColumns;
    private String parentIdColumnName;

    public TwoUnionRelationTransferOperator(LinkedHashMap<String, Integer> columns, String idColumnName, List<String> showColumns,
                                            String[] addedColumns, String parentIdColumnName) {
        this.columns = columns;
        this.idColumnName = idColumnName;
        this.showColumns = showColumns;
        this.addedColumns = addedColumns;
        this.parentIdColumnName = parentIdColumnName;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new TwoUnionRelationOperatorResultSet(this.columns, this.idColumnName, this.showColumns, this.addedColumns, this.parentIdColumnName, basedSegments.get(0), metaData);
    }
}
