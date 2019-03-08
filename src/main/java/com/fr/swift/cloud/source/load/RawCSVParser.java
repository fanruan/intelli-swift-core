package com.fr.swift.cloud.source.load;

import au.com.bytecode.opencsv.CSVParser;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2019/3/7.
 */
public class RawCSVParser implements LineParser {

    private CSVParser parser = new CSVParser();
    private List<SwiftMetaDataColumn> fields;

    public RawCSVParser(List<SwiftMetaDataColumn> fields) {
        this.fields = fields;
    }

    @Override
    public Map<String, Object> parseToMap(String input) {
        try {
            String[] row = parser.parseLine(input);
            if (row != null && row.length - 1 == fields.size()) {
                return ParseUtils.parseFields(row, fields);
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("fail to parse line: {}", input, e);
        }
        return null;
    }

    @Override
    public List<SwiftMetaDataColumn> getFields() {
        return fields;
    }
}
