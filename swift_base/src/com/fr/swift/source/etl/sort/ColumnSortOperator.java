package com.fr.swift.source.etl.sort;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-31 17:07:05
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ColumnSortOperator extends AbstractOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ColumnSortOperator.class);

    @CoreField
    private Map<String, Integer> fieldsSortedMap;

    public ColumnSortOperator(Map<String, Integer> fieldsSortedMap) {
        this.fieldsSortedMap = fieldsSortedMap;
    }

    public Map<String, Integer> getFieldsSortedMap() {
        return fieldsSortedMap;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        return new ArrayList<SwiftMetaDataColumn>();
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.SORT;
    }
}
