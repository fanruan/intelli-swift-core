package com.fr.swift.utils;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.etl.ETLSource;

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

}
