package com.fr.swift.source.etl.columnfilter;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/24 0024 15:22
 */
public class ColumnFilterOperator extends AbstractOperator {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ColumnFilterOperator.class);
    @CoreField
    private FilterInfo filterInfo;

    public ColumnFilterOperator(FilterInfo filterInfo) {
        this.filterInfo = filterInfo;
    }

    public FilterInfo getFilterInfo() {
        return this.filterInfo;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        try {
            for (SwiftMetaData metaData : metaDatas) {
                for (int j = 0; j < metaData.getColumnCount(); j++) {
                    columnList.add(metaData.getColumn(j + 1));
                }
            }
        } catch (SwiftMetaDataException e) {
            LOGGER.error("getting meta's column information failed", e);
        }
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.FILTER;
    }
}
