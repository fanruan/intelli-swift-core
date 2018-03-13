package com.fr.swift.source.etl.datamining;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.etl.datamining.timeseries.arima.ArimaOperator;
import com.fr.swift.source.etl.datamining.timeseries.holtwinter.HoltWinterOperator;
import com.fr.swift.source.etl.datamining.timeseries.regression.RegressionOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 10:45
 */
public class DataMiningOperator extends AbstractOperator {

    private AlgorithmBean algorithmBean = null;

    private AlgorithmMetaData algorithmOperator = null;

    public DataMiningOperator(AlgorithmBean algorithmBean) {
        this.algorithmBean = algorithmBean;
        init();
    }

    private void init(){
        switch (algorithmBean.getAlgorithmName()){
            case ARIMA:
                algorithmOperator = new ArimaOperator(algorithmBean);
                break;
            case MULTI_REGRESSION:
                algorithmOperator = new RegressionOperator(algorithmBean);
                break;
            case HOLT_WINTERS:
                algorithmOperator = new HoltWinterOperator(algorithmBean);
                break;
            default:
                break;
        }
    }

    public AlgorithmBean getAlgorithmBean() {
        return this.algorithmBean;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        return algorithmOperator.getColumns(tables);
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.DATAMINING;
    }
}
