package com.fr.swift.utils;

import com.fr.general.ComparatorUtils;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.source.etl.OperatorType;

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
                OperatorType type = etlOperator.getOperatorType();
                if (type.isAddColumn()) {
                    List<String> names = etlOperator.getNewAddedName();
                    if (etlSource.getFieldsInfo() != null) {
                        for (int i = 0; i < names.size(); i++) {
                            if (ComparatorUtils.equals(etlSource.getFieldsInfo().get(i), names.get(i))) {
                                fields.add(names.get(i));
                            } else {
                                fields.add(etlSource.getFieldsInfo().get(i));
                            }
                        }
                    } else {
                        fields.addAll(names);
                    }
                }
            }
            return fields;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return fields;
        }
    }

}
