package com.fr.swift.source.etl.valueconverter;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/2/23 0023 11:53
 */
public class ValueConverterTransferOperator implements ETLTransferOperator {

    private String column;
    private int columnType;

    public ValueConverterTransferOperator(String column, int columnType) {
        this.column = column;
        this.columnType = columnType;
    }


    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new ValueConverterResultSet(column, columnType, basedSegments.get(0), metaData);
    }
}
