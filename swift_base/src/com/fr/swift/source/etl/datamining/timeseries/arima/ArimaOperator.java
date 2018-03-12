package com.fr.swift.source.etl.datamining.timeseries.arima;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.datamining.AlgorithmMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class ArimaOperator implements AlgorithmMetaData {

    private AlgorithmBean algorithmBean;

    public ArimaOperator(AlgorithmBean algorithmBean) {
        this.algorithmBean = algorithmBean;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        SwiftMetaData table = tables[0];
        try{

            for(int i = 0;i < table.getColumnCount(); ++i){
                columnList.add(tables[0].getColumn(i+1));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return columnList;
    }
}
