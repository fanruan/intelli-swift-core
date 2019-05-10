package com.fr.swift.cloud.source.load;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
public class RawGCParser implements LineParser<Row> {

    private List<SwiftMetaDataColumn> fields;

    public RawGCParser(List<SwiftMetaDataColumn> fields) {
        this.fields = fields;
    }

    @Override
    public Map<String, Object> parseToMap(Row input) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < input.getSize(); i++) {
            map.put(String.valueOf(i), input.getValue(i));
        }
        return map;
    }

    @Override
    public List<SwiftMetaDataColumn> getFields() {
        return fields;
    }
}
