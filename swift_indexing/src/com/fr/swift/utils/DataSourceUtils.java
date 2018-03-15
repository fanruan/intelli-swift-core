package com.fr.swift.utils;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.etl.date.GetFromDateOperator;
import com.fr.swift.source.etl.datediff.DateDiffOperator;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;
import com.fr.swift.source.etl.rowcal.accumulate.AccumulateRowOperator;
import com.fr.swift.source.etl.rowcal.alldata.AllDataRowCalculatorOperator;
import com.fr.swift.source.etl.rowcal.rank.RankRowOperator;

import java.util.ArrayList;
import java.util.List;

public class DataSourceUtils {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DataSourceUtils.class);

    public static String getSwiftSourceKey(DataSource dataSource) {
        try {
            if (dataSource instanceof ETLSource) {
                ETLSource etlSource = (ETLSource) dataSource;
                if (etlSource.getOperator().getOperatorType().isAddColumn()) {
                    assert etlSource.getBasedSources().size() == 1;
                    DataSource baseDataSource = etlSource.getBasedSources().get(0);
                    return getSwiftSourceKey(baseDataSource);
                }
            }
            return dataSource.getSourceKey().getId();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return dataSource.getSourceKey().getId();
        }
    }

    public static boolean isAddColumn(DataSource dataSource) {
        try {
            if (dataSource instanceof ETLSource) {
                ETLSource etlSource = (ETLSource) dataSource;
                return etlSource.getOperator().getOperatorType().isAddColumn();
            }
            return false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    public static List<String> getAddFields(DataSource dataSource) {
        List<String> fields = new ArrayList<String>();
        try {
            if (dataSource instanceof ETLSource) {
                ETLSource etlSource = (ETLSource) dataSource;
                ETLOperator etlOperator = etlSource.getOperator();
<<<<<<< HEAD
                OperatorType type = etlOperator.getOperatorType();
                switch(type) {
                    case COLUMN_FORMULA:
                        fields.add(((ColumnFormulaOperator) etlOperator).getColumnMD5());
                        break;
                    case ACCUMULATE:
                        fields.add(((AccumulateRowOperator) etlOperator).getColumnMD5());
                        break;
                    case ALLDATA:
                        fields.add(((AllDataRowCalculatorOperator) etlOperator).getColumnMD5());
                        break;
                    case RANK:
                        fields.add(((RankRowOperator) etlOperator).getColumnMD5());
                        break;
                    case GETDATE:
                        fields.add(((GetFromDateOperator) etlOperator).getColumnMD5());
                        break;
                    case DATEDIFF:
                        fields.add(((DateDiffOperator) etlOperator).getColumnMD5());
                        break;
=======
                if (etlOperator.getOperatorType().equals(OperatorType.COLUMN_FORMULA)) {
                    fields.add(((ColumnFormulaOperator) etlOperator).getColumnName());
>>>>>>> fc0231d0300a772eb2341eeeb2f1c764958c5495
                }
            }
            return fields;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return fields;
        }
    }

}
