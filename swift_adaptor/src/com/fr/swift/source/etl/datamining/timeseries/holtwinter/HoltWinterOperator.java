package com.fr.swift.source.etl.datamining.timeseries.holtwinter;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.datamining.AlgorithmMetaData;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class HoltWinterOperator implements AlgorithmMetaData {
    private AlgorithmBean algorithmBean = null;

    public HoltWinterOperator(AlgorithmBean algorithmBean) {
        this.algorithmBean = algorithmBean;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn("时间列", Types.VARCHAR));
        columnList.add(new MetaDataColumn("观测值", Types.VARCHAR));
        columnList.add(new MetaDataColumn("预测值", Types.DOUBLE));
        return columnList;
    }
}
