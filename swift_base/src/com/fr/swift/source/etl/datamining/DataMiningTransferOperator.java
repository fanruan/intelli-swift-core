package com.fr.swift.source.etl.datamining;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 10:52
 */
public class DataMiningTransferOperator implements ETLTransferOperator {

    private List<List<ColumnKey>> lists;

    public DataMiningTransferOperator(List<List<ColumnKey>> lists) {
        this.lists = lists;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        List<List<Segment>> tis = new ArrayList<List<Segment>>();
        for (int i = 0; i < basedSegments.size(); i++) {
            tis.add(Arrays.asList(basedSegments.get(i)));
        }
        return new DataMiningOperatorResultSet(lists, tis, metaData);
    }
}
