package com.fr.swift.source.split.json;

import com.fr.swift.source.split.SubRow;
import com.fr.swift.util.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lucifer
 * @date 2019/7/26
 * @description
 * @since swift 1.1
 */
public class JsonSubRow implements SubRow {

    private String splitColumn;

    private Map<String, Object> subRow = new HashMap<>();

    /**
     * for combine
     */
    public JsonSubRow() {
        this.splitColumn = Strings.EMPTY;
    }

    /**
     * for parse
     *
     * @param splitColumn
     * @param splitRow
     */
    public JsonSubRow(String splitColumn, Map<String, Object> splitRow) {
        this.splitColumn = splitColumn;
        spiltSubFields(splitRow);
    }

    private void spiltSubFields(Map<String, Object> splitRow) {
        for (Map.Entry<String, Object> columnEntrty : splitRow.entrySet()) {
            String subField = splitColumn + "." + columnEntrty.getKey();
            subRow.put(subField, columnEntrty.getValue());
        }
    }

    @Override
    public void combineSplitRow(SubRow subRow) {
        this.subRow.putAll(subRow.getSubRow());
    }

    public Map<String, Object> getSubRow() {
        return new HashMap<>(subRow);
    }
}