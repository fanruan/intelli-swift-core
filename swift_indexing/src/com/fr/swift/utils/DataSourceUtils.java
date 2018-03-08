package com.fr.swift.utils;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;

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
                if (etlOperator.getOperatorType().equals(OperatorType.COLUMN_FORMULA)) {
                    fields.add(((ColumnFormulaOperator) etlOperator).getColumnMD5());
                }
            }
            return fields;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return fields;
        }
    }

}
