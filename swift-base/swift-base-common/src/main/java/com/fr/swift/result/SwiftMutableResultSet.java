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
    public SwiftMutableResultSet(SwiftMetaData baseMetadata, SwiftResultSet swiftResultSet, ColumnSplitRule[] columnSplitRules) {
        this.baseMetadata = baseMetadata;
        this.swiftResultSet = swiftResultSet;
        this.columnSplitRules = columnSplitRules;
        this.currentSubFields = new ArrayList<>();
        this.columnMap = new HashMap<>();
    }

    public void changeResultSet(SwiftResultSet resultSet) {
        swiftResultSet = resultSet;
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
        return new SwiftMetaDataBean.Builder()
                .setSwiftSchema(baseMetadata.getSwiftSchema())
                .setTableName(baseMetadata.getTableName())
                .setFields(swiftMetaDataColumnList)
                .build();
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
                Object value1;
                if (originValue instanceof Number) {
                    value1 = ((Number) originValue).doubleValue();
                } else {
                    value1 = String.valueOf(originValue);
                }
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

    /**
     * 动态解析 Json 字段的类型：
     * Number -> Double
     * not Number -> String
     */
    private SwiftMetaDataColumn getColumn(Map.Entry<String, Object> entry) {
        if (entry.getValue() instanceof Number) {
            entry.setValue(((Number) entry.getValue()).doubleValue());
            return MetaDataColumnBean.ofDouble(entry.getKey());
        } else {
            entry.setValue(String.valueOf(entry.getValue()));
            return MetaDataColumnBean.ofString(entry.getKey());
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
}