package com.fr.swift.source.etl.datamining;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.etl.datamining.timeseries.arima.ArimaOperator;
import com.fr.swift.source.etl.datamining.timeseries.holtwinter.HoltWinterOperator;
import com.fr.swift.source.etl.datamining.timeseries.regression.RegressionOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class DataMiningOperator extends AbstractOperator {

    private AlgorithmBean algorithmBean = null;

    private AlgorithmMetaData algorithmOperator = null;

    public DataMiningOperator(AlgorithmBean algorithmBean) {
        this.algorithmBean = algorithmBean;
        init();
    }

    private void init() {
        switch (algorithmBean.getAlgorithmName()) {
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
        return OperatorType.EXTRA;
    }

    @Override
    public List<String> getNewAddedName() {
        List<String> addColumnNames = new ArrayList<String>();
        return addColumnNames;
    }
}
