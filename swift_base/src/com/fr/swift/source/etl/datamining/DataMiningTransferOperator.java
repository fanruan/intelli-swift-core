package com.fr.swift.source.etl.datamining;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;
import com.fr.swift.source.etl.datamining.timeseries.arima.ArimaResultSet;
import com.fr.swift.source.etl.datamining.timeseries.holtwinter.HoltWinterResultSet;
import com.fr.swift.source.etl.datamining.timeseries.regression.RegressionResultSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 10:52
 */
public class DataMiningTransferOperator implements ETLTransferOperator {

    private AlgorithmBean algorithmBean;

    public DataMiningTransferOperator(AlgorithmBean ab) {
        this.algorithmBean = ab;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        List<List<Segment>> tis = new ArrayList<List<Segment>>();
        for (int i = 0; i < basedSegments.size(); i++) {
            tis.add(Arrays.asList(basedSegments.get(i)));
        }
        switch (algorithmBean.getAlgorithmName()){
            case ARIMA:
                return new ArimaResultSet(algorithmBean, metaData,basedMetas.get(0), tis.get(0));
            case REGRESSION:
                return new RegressionResultSet(algorithmBean, tis, metaData);
            case HOLTWINTERS:
                return new HoltWinterResultSet(algorithmBean, tis, metaData);
            default:
                return null;
        }
    }
}
