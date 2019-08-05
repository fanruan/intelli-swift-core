package com.fr.swift.source.split.json;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.split.ColumnSplitRule;

import java.util.Map;

/**
 * @author lucifer
 * @date 2019/7/26
 * @description
 * @since swift 1.1
 */
public class JsonColumnSplitRule implements ColumnSplitRule<Row, JsonSubRow> {

    private String splitColumn;
    private SwiftMetaData baseMetadata;
    private int columnIndex;

    public JsonColumnSplitRule(String splitColumn, SwiftMetaData baseMetadata) throws SwiftMetaDataException {
        this.splitColumn = splitColumn;
        this.baseMetadata = baseMetadata;
        this.columnIndex = this.baseMetadata.getColumnIndex(splitColumn);
    }

    @Override
    public JsonSubRow split(Row row) throws Exception {
        String json = row.getValue(columnIndex - 1);
        Map<String, Object> splitResult = JsonBuilder.readValue(json, Map.class);
        return new JsonSubRow(splitColumn, splitResult);
    }
}
