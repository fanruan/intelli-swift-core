package com.fr.swift.source.etl.datamining.timeseries.regression;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.datamining.AlgorithmMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class RegressionOperator implements AlgorithmMetaData {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RegressionOperator.class);
    private AlgorithmBean ab = null;

    public RegressionOperator(AlgorithmBean ab) {
        this.ab = ab;
    }

    public AlgorithmBean getColumnKeyList() {
        return this.ab;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        return columnList;
    }
}
