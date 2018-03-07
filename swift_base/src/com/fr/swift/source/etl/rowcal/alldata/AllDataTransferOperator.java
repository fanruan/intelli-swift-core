package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;
import com.fr.swift.source.etl.utils.ETLConstant;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.List;

/**
 * Created by Handsome on 2018/2/24 0024 15:04
 */
public class AllDataTransferOperator implements ETLTransferOperator {

    private int summaryType;
    private String columnName;
    private int columnType;
    private ColumnKey[] dimensions;

    public AllDataTransferOperator(int summaryType, String columnName, int columnType, ColumnKey[] dimensions) {
        this.summaryType = summaryType;
        this.columnName = columnName;
        this.columnType = columnType;
        this.dimensions = dimensions;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        AllDataCalculator cal = createCalculator(summaryType);
        Segment[] segments = basedSegments.get(0);
        RowTraversal[] traversals = new RowTraversal[segments.length];
        for(int i = 0; i < traversals.length; i++) {
            traversals[i] = segments[i].getAllShowIndex();
        }
        return new AllDataRowCalculatorResultSet(columnName, segments, traversals, metaData, cal, dimensions);
    }

    private AllDataCalculator createCalculator(int type) {
        switch(type){
            case ETLConstant.CONF.GROUP.NUMBER.SUM : {
                return SumCalculator.INSTANCE;
            }
            case ETLConstant.CONF.GROUP.NUMBER.MAX : {
                return MaxCalculator.INSTANCE;
            }
            case ETLConstant.CONF.GROUP.NUMBER.MIN : {
                return MinCalculator.INSTANCE;
            }
            case ETLConstant.CONF.GROUP.NUMBER.AVG : {
                return AvgCalculator.INSTANCE;
            }
            case ETLConstant.CONF.GROUP.NUMBER.COUNT: {
                return CountCalculator.INSTANCE;
            }
            default: {
                return SumCalculator.INSTANCE;
            }
        }
    }
}
