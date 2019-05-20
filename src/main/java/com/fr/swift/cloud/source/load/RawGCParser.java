package com.fr.swift.cloud.source.load;

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
public class RawGCParser implements LineParser<String> {

    private List<SwiftMetaDataColumn> fields;

    public RawGCParser(List<SwiftMetaDataColumn> fields) {
        this.fields = fields;
    }

    @Override
    public Map<String, Object> parseToMap(String input) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        String[] lines = input.split(";");
        for (String line : lines) {
            if (line.contains("minor GC") || line.contains("major GC")) {
                String[] rows = line.split(",");
                map.put("gcStartTime", Long.valueOf(rows[0].substring(rows[0].indexOf("(") + 1, rows[0].indexOf(")"))));
                map.put("gcEndTime", Long.valueOf(rows[2].substring(rows[2].indexOf("(") + 1, rows[2].indexOf(")"))));
                map.put("gcType", rows[3].substring(rows[3].indexOf("[") + 1, rows[3].indexOf("GC") + 2));
                map.put("pid", Long.valueOf(rows[4].substring(rows[4].indexOf("pid:") + 5, rows[4].indexOf("]"))));
            } else if (line.contains("[PS Eden Space]")) {
                map.put("psEdenSpace", line.trim());
            } else if (line.contains("[Code Cache]")) {
                map.put("codeCache", line.trim());
            } else if (line.contains("[Compressed Class Space]")) {
                map.put("classSpace", line.trim());
            } else if (line.contains("[PS Survivor Space]")) {
                map.put("psSurvivorSpace", line.trim());
            } else if (line.contains("[PS Old Gen]")) {
                map.put("psOldGen", line.trim());
            } else if (line.contains("[Metaspace]")) {
                map.put("metaSpace", line.trim());
            } else if (line.contains("[PSYoungGen")) {
                String[] rows = line.split(",");
                for (String row : rows) {
                    if (row.contains("[PSYoungGen")) {
                        map.put("psYoungGen", row.trim());
                    } else if (row.contains("[ParOldGen")) {
                        map.put("parOldGen", row.trim());
                    } else if (row.contains("[Heap")) {
                        map.put("heap", row.trim());
                    }
                }
            } else if (line.contains("duration")) {
                long duration = Long.valueOf(line.substring(line.indexOf("duration:") + 9, line.indexOf("ms")));
                map.put("duration", duration);
            }
        }
        return map;
    }

    @Override
    public List<SwiftMetaDataColumn> getFields() {
        return fields;
    }
}
