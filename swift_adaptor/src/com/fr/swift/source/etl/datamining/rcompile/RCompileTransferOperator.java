package com.fr.swift.source.etl.datamining.rcompile;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/3/30 0030 11:38
 */
public class RCompileTransferOperator implements ETLTransferOperator {

    private String[] columns;
    private int[] columnType;
    private List dataList;

    public RCompileTransferOperator(String[] columns, int[] columnType, List dataList) {
        this.columns = columns;
        this.columnType = columnType;
        this.dataList = dataList;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new RCompileResultSet(columns, columnType, dataList, metaData);
    }
}
