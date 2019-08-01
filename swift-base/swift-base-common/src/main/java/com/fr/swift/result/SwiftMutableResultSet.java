package com.fr.swift.result;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.ListMutableRow;
import com.fr.swift.source.MutableRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.split.ColumnSplitRule;
import com.fr.swift.source.split.SubRow;
import com.fr.swift.source.split.json.JsonSubRow;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lucifer
 * @date 2019/7/26
 * @description
 * @since swift 1.1
 */
public class SwiftMutableResultSet implements MutableResultSet {

    private SwiftMetaData baseMetadata;

    private ColumnSplitRule[] columnSplitRules;

    private SwiftResultSet swiftResultSet;

    private List<String> currentSubFields;

    private Map<String, SwiftMetaDataColumn> columnMap;

    private boolean hasNewSubfields = false;

    /**
     * @param baseMetadata
     * @param swiftResultSet
     * @param columnSplitRules
     * @description 外部需要保证baseNetadata的列数和row的列数相同，没有的列填null。
     */
    public SwiftMutableResultSet(SwiftMetaData baseMetadata, SwiftResultSet swiftResultSet, ColumnSplitRule... columnSplitRules) {
        this.baseMetadata = baseMetadata;
        this.swiftResultSet = swiftResultSet;
        this.columnSplitRules = columnSplitRules;
        this.currentSubFields = new ArrayList<>();
        this.columnMap = new HashMap<>();
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        List<SwiftMetaDataColumn> swiftMetaDataColumnList = new ArrayList<>();
        for (int i = 1; i <= baseMetadata.getColumnCount(); i++) {
            swiftMetaDataColumnList.add(baseMetadata.getColumn(i));
        }
        for (String currentSubField : currentSubFields) {
            try {
                baseMetadata.getColumnIndex(currentSubField);
            } catch (SwiftMetaDataColumnAbsentException e) {
                swiftMetaDataColumnList.add(columnMap.get(currentSubField));
            }
        }
        return new SwiftMetaDataBean(baseMetadata.getSwiftSchema(), baseMetadata.getTableName(), swiftMetaDataColumnList);
    }

    @Override
    public boolean hasNext() throws SQLException {
        hasNewSubfields = false;
        return swiftResultSet.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        Row baseRow = swiftResultSet.getNextRow();
        if (columnSplitRules.length == 0) {
            return baseRow;
        }
        MutableRow mutableRow = new ListMutableRow(baseRow);

        SubRow subRow = new JsonSubRow();
        for (ColumnSplitRule columnSplitRule : columnSplitRules) {
            try {
                subRow.combineSplitRow(columnSplitRule.split(baseRow));
            } catch (Exception ignore) {
                continue;
            }
        }
        parseMutableRow(mutableRow, subRow);
        return mutableRow;
    }

    private void parseMutableRow(MutableRow mutableRow, SubRow subRow) throws SwiftMetaDataException {
        Map<String, Object> subRowMap = subRow.getSubRow();

        for (String currentSubField : currentSubFields) {
            if (subRowMap.containsKey(currentSubField)) {
                Object originValue = subRowMap.get(currentSubField);
                Object value1 = isNumber(originValue) ? Double.parseDouble(String.valueOf(originValue)) : originValue;
                addOrSetValue(mutableRow, currentSubField, value1);
            } else {
                addOrSetValue(mutableRow, currentSubField, null);
            }
            subRowMap.remove(currentSubField);
        }
        for (Map.Entry<String, Object> newSubEntry : subRowMap.entrySet()) {
            currentSubFields.add(newSubEntry.getKey());
            columnMap.put(newSubEntry.getKey(), getColumn(newSubEntry));
            addOrSetValue(mutableRow, newSubEntry.getKey(), newSubEntry.getValue());
            this.hasNewSubfields = true;
        }
    }

    @Override
    public void close() throws SQLException {
        swiftResultSet.close();
    }

    @Override
    public boolean hasNewSubfields() {
        return hasNewSubfields;
    }

    private SwiftMetaDataColumn getColumn(Map.Entry<String, Object> entry) {
        if (isNumber(entry.getValue())) {
            entry.setValue(Double.parseDouble(String.valueOf(entry.getValue())));
            return new MetaDataColumnBean(entry.getKey(), Types.DOUBLE);
        } else {
            return new MetaDataColumnBean(entry.getKey(), Types.VARCHAR);
        }
    }

    private void addOrSetValue(MutableRow mutableRow, String field, Object value) throws SwiftMetaDataException {
        try {
            int index = baseMetadata.getColumnIndex(field);
            mutableRow.setElement(index - 1, value);
        } catch (SwiftMetaDataColumnAbsentException e) {
            mutableRow.addElement(value);
        }
    }

    private boolean isNumber(Object value) {
        if (value instanceof String) {
            return false;
        }
        return true;
    }
}