package com.fr.swift.cloud.source.load;

import com.fr.swift.annotation.Compatible;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
public class RawGCParser implements LineParser<String> {

    private List<SwiftMetaDataColumn> fields;

    private static Pattern pattern = Pattern.compile("\\d+");

    private static String[] youngKeys = {"youngBefore", "youngAfter", "youngTotal"};
    private static String[] oldKeys = {"oldBefore", "oldAfter", "oldTotal"};
    private static String[] metaKeys = {"metaspaceBefore", "metaspaceAfter", "metaspaceTotal"};
    private static String[] sumKeys = {"sumBefore", "sumAfter", "sumTotal"};

    public RawGCParser(List<SwiftMetaDataColumn> fields) {
        this.fields = fields;
    }

    @Override
    public Map<String, Object> parseToMap(String input) {
        if (input.contains("timestamp")) {
            return parseNew(input);
        } else {
            return parseOld(input);
        }
    }

    @Override
    public List<SwiftMetaDataColumn> getFields() {
        return fields;
    }

    private Map<String, Object> parseNew(String input) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        if (input.contains("Full GC")) {
            map.put("gcType", "full GC");
        } else {
            map.put("gcType", "minor GC");
        }
        List<String> elementList = new ArrayList<>();
        for (String columnAll : input.split("\\[")) {
            String[] columns = columnAll.split("\\]");
            for (String column : columns) {
                elementList.add(column.trim());
            }
        }
        for (String element : elementList) {
            if (element.startsWith("timestamp:")) {
                map.put("gcStartTime", Long.valueOf(element.substring(("timestamp:").length()).trim()));
            } else if (element.startsWith("pid:")) {
                map.put("pid", Long.valueOf(element.substring(("pid:").length()).trim()));
            } else if (element.startsWith("Times: real=")) {
                long duration = (long) (Double.valueOf(element.substring(("Times: real=").length(), element.indexOf("secs")).trim()) * 1000);
                map.put("duration", duration);
            } else if (element.contains("->")) {
                if (element.startsWith("PSYoungGen")) {
                    fillMap(map, "PSYoungGen", element);
                } else if (element.startsWith("ParOldGen")) {
                    fillMap(map, "ParOldGen", element);
                } else if (element.startsWith("Metaspace")) {
                    fillMap(map, "Metaspace", element);
                } else {
                    fillMap(map, "SUM", element);
                }
            } else if (element.contains("node")) {
                map.put("node", element.substring(("node:").length()).trim());
            }
        }
        return map;
    }

    private void fillMap(Map<String, Object> map, String matchName, String element) {
        String[] keys;
        if (matchName.startsWith("PSYoungGen")) {
            keys = youngKeys;
        } else if (matchName.startsWith("ParOldGen")) {
            keys = oldKeys;
        } else if (matchName.startsWith("Metaspace")) {
            keys = metaKeys;
        } else {
            keys = sumKeys;
        }
        Matcher matcher = pattern.matcher(element);
        for (String key : keys) {
            if (matcher.find()) {
                map.put(key, Long.valueOf(matcher.group()));
            }
        }
    }

    //待旧gc日志全部升级后再移除旧的解析方式
    @Compatible
    private Map<String, Object> parseOld(String input) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        String[] lines = input.split(";");
        for (String line : lines) {
            if (line.contains("minor GC") || line.contains("major GC")) {
                String[] rows = line.split(",");
                map.put("gcStartTime", Long.valueOf(rows[0].substring(rows[0].indexOf("(") + 1, rows[0].indexOf(")"))));
                if (line.contains("minor GC")) {
                    map.put("gcType", "minor GC");
                } else if (line.contains("major GC")) {
                    map.put("gcType", "major GC");
                }
                map.put("pid", Long.valueOf(rows[4].substring(rows[4].indexOf("pid:") + 5, rows[4].indexOf("]"))));
            } else if (line.contains("duration")) {
                long duration = Long.valueOf(line.substring(line.indexOf("duration:") + 9, line.indexOf("ms")));
                map.put("duration", duration);
            }
        }
        return map;
    }
}
