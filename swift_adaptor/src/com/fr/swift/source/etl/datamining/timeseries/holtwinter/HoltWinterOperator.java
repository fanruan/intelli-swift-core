package com.fr.swift.source.etl.datamining.timeseries.holtwinter;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.timeseries.HoltWintersBean;
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
    private HoltWintersBean algorithmBean = null;

    public HoltWinterOperator(AlgorithmBean algorithmBean) {
        this.algorithmBean = (HoltWintersBean) algorithmBean;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        SwiftMetaData table = tables[0];
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        String timeFieldName = algorithmBean.getTimeFieldName();
        try {
            if (isDateType(table.getColumn(timeFieldName).getType())) {
                columnList.add(new MetaDataColumn(timeFieldName, Types.TIMESTAMP));
            } else {
                columnList.add(new MetaDataColumn(timeFieldName, Types.INTEGER));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            columnList.add(new MetaDataColumn(timeFieldName, Types.TIMESTAMP));
        }
        for (String groupName : algorithmBean.getGroupFields()) {
            columnList.add(new MetaDataColumn(groupName, Types.VARCHAR));
        }
        columnList.add(new MetaDataColumn(algorithmBean.getTargetFiledName(), Types.DOUBLE));
        columnList.add(new MetaDataColumn("forecast", Types.DOUBLE));
        return columnList;
    }

    public static boolean isDateType(int sqlType) {
        switch (sqlType) {
            case Types.DATE:
            case Types.TIMESTAMP:
            case Types.TIME:
                return true;
        }
        return false;
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
