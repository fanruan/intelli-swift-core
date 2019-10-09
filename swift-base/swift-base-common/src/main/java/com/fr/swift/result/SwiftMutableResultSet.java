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
import com.fr.swift.util.Strings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

    protected SwiftResultSet swiftResultSet;

    /**
     * key: sub fieldName  value:sub metadata
     */
    private Map<String, SwiftMetaDataColumn> subColumnMap;

    /**
     * key:sub tableName   value:sub metadata
     */
    private Map<String, SwiftMetaData> subMetadataMap;

    /**
     * key: sub id   value:sub tableName
     */
    private Map<String, String> subTableNameMap;
    private String currentTableName;

    /**
     * key:sub tableName  value:current sub fieldNames
     */
    private Map<String, LinkedHashSet<String>> currentSubFieldMap;


    private boolean hasNewSubfields = false;

    /**
     * 提前预初始化一下可能的子表和字段信息
     *
     * @param baseMetadata
     * @param swiftResultSet
     * @param columnSplitRules
     * @param subMetadataMap
     */
    public SwiftMutableResultSet(SwiftMetaData baseMetadata, SwiftResultSet swiftResultSet, ColumnSplitRule[] columnSplitRules, Map<String, SwiftMetaData> subMetadataMap) throws SwiftMetaDataException {
        this(baseMetadata, swiftResultSet, columnSplitRules);
        if (subMetadataMap != null) {
            List<String> baseFields = baseMetadata.getFieldNames();
            for (Map.Entry<String, SwiftMetaData> subMetadataEntry : subMetadataMap.entrySet()) {
                if (!subMetadataEntry.getKey().equals(baseMetadata.getTableName())) {
                    this.subMetadataMap.put(subMetadataEntry.getKey(), subMetadataEntry.getValue());
                    SwiftMetaData subMetadata = subMetadataEntry.getValue();
                    this.currentSubFieldMap.put(subMetadata.getTableName(), new LinkedHashSet<String>());
                    for (int i = 0; i < subMetadata.getColumnCount(); i++) {
                        SwiftMetaDataColumn subColumn = subMetadata.getColumn(i + 1);
                        if (!baseFields.contains(subColumn.getName())) {
                            this.subColumnMap.put(subColumn.getName(), subColumn);
                            this.currentSubFieldMap.get(subMetadata.getTableName()).add(subColumn.getName());
                        }
                    }
                }
            }
        }
    }

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
        this.subColumnMap = new HashMap<>();
        this.subMetadataMap = new HashMap<>();
        this.subTableNameMap = new HashMap<>();
        this.currentTableName = Strings.EMPTY;
        this.currentSubFieldMap = new HashMap<>();
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        if (hasNewSubfields || !subMetadataMap.containsKey(currentTableName)) {
            List<SwiftMetaDataColumn> swiftMetaDataColumnList = new ArrayList<>();
            for (int i = 1; i <= baseMetadata.getColumnCount(); i++) {
                swiftMetaDataColumnList.add(baseMetadata.getColumn(i));
            }
            for (String currentSubField : currentSubFieldMap.get(currentTableName)) {
                try {
                    baseMetadata.getColumnIndex(currentSubField);
                } catch (SwiftMetaDataColumnAbsentException e) {
                    swiftMetaDataColumnList.add(subColumnMap.get(currentSubField));
                }
            }
            SwiftMetaData newMetaData = new SwiftMetaDataBean.Builder()
                    .setSwiftSchema(baseMetadata.getSwiftSchema())
                    .setTableName(currentTableName)
                    .setFields(swiftMetaDataColumnList)
                    .build();
            subMetadataMap.put(currentTableName, newMetaData);

        }
        return subMetadataMap.get(currentTableName);
    }

    @Override
    public boolean hasNext() throws SQLException {
        hasNewSubfields = false;
        return swiftResultSet.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        Row baseRow = swiftResultSet.getNextRow();
        // TODO: 2019/10/8 先默认第一个字段
        String id = baseRow.getValue(0);
        if (!this.subTableNameMap.containsKey(id)) {
            subTableNameMap.put(id, buildSubTableName(baseMetadata.getTableName(), id));
        }
        this.currentTableName = this.subTableNameMap.get(id);
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
        if (!currentSubFieldMap.containsKey(currentTableName)) {
            currentSubFieldMap.put(currentTableName, new LinkedHashSet<String>());
        }
        for (String currentSubField : currentSubFieldMap.get(currentTableName)) {
            if (subRowMap.containsKey(currentSubField)) {
                Object originValue = subRowMap.get(currentSubField);
                Object value1;
                if (originValue == null) {
                    value1 = null;
                } else if (originValue instanceof Number) {
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
            currentSubFieldMap.get(currentTableName).add(newSubEntry.getKey());
            subColumnMap.put(newSubEntry.getKey(), getColumn(newSubEntry));
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

    @Override
    public String getCurrentTableName() {
        return currentTableName;
    }

    protected String buildSubTableName(String baseTableName, String id) {
        if (id == null) {
            return baseTableName + "_" + "null";
        }
        if (id.startsWith("FR")) {
            return baseTableName + "_" + id;
        } else {
            return baseTableName + "_" + "function";
        }
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