package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.segment.Segment;
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
    private int rule;
    private String columnName;
    private int columnType;

    public AllDataTransferOperator(int summaryType, int rule, String columnName, int columnType) {
        this.summaryType = summaryType;
        this.rule = rule;
        this.columnName = columnName;
        this.columnType = columnType;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        AllDataCalculator cal = createCalculator(summaryType);
        Segment[] segments = basedSegments.get(0);
        RowTraversal[] traversals = new RowTraversal[segments.length];
        for(int i = 0; i < traversals.length; i++) {
            traversals[i] = segments[i].getAllShowIndex();
        }
        return new AllDataRowCalculatorResultSet(columnName, columnType, segments, traversals, metaData, cal);
    }

    private AllDataCalculator createCalculator(int type) {
        switch(type){
            case ETLConstant.SUMMARY_TYPE.SUM : {
                return SumCalculator.INSTANCE;
            }
            case ETLConstant.SUMMARY_TYPE.MAX : {
                return MaxCalculator.INSTANCE;
            }
            case ETLConstant.SUMMARY_TYPE.MIN : {
                return MinCalculator.INSTANCE;
            }
            case ETLConstant.SUMMARY_TYPE.AVG : {
                return AvgCalculator.INSTANCE;
            }
            case ETLConstant.SUMMARY_TYPE.COUNT : {
                return CountCalculator.INSTANCE;
            }
            default: {
                return SumCalculator.INSTANCE;
            }
        }
    }
}
