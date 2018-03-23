package com.fr.swift.source.etl.datamining.timeseries.holtwinter;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class HoltWinterOperator extends AbstractOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(HoltWinterOperator.class);
    private AlgorithmBean algorithmBean = null;
    private String columnName = "forecast";

    public HoltWinterOperator(AlgorithmBean algorithmBean) {
        this.algorithmBean = algorithmBean;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        SwiftMetaData table = tables[0];
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn("时间列", Types.DATE));
        columnList.add(new MetaDataColumn("分组列", Types.VARCHAR));
        columnList.add(new MetaDataColumn("实际值", Types.DOUBLE));

        columnList.add(new MetaDataColumn("预测值",Types.DOUBLE));
//        try {
//            for (int i = 0; i < table.getColumnCount(); i++) {
//                columnList.add(table.getColumn(i+1));
//            }
//            columnList.add(new MetaDataColumn("预测值",Types.DOUBLE));
//        } catch (SwiftMetaDataException e) {
//            LOGGER.error(e.getMessage(),e);
//        }
//        columnList.add(new MetaDataColumn(this.columnName,
//                this.columnName, Types.DOUBLE, MD5Utils.getMD5String(new String[]{(this.columnName)})));

        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.EXTRA_FALSE;
    }

    @Override
    public List<String> getNewAddedName() {
        return null;
    }
}
