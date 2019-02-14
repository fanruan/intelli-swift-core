package com.fr.swift.base.json.writer.impl;

import com.fr.swift.base.json.writer.JsonWriter;

import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018-12-12
 */
public class MapJsonWriter implements JsonWriter<Map> {
    private static final ObjectJsonWriter OBJECT_JSON_WRITER = new ObjectJsonWriter();

    @Override
    public String write(Map map) {
        StringBuffer buffer = new StringBuffer(1024);
        writeMap(buffer, map);
        return buffer.toString();
    }

    private void writeMap(StringBuffer buffer, Map map) {
        if (map == null) {
            buffer.append("{}");
            return;
        }
        if (map.isEmpty()) {
            buffer.append("{}");
            return;
        }
        buffer.append('{');
        boolean isFirst = true;
        Set<Map.Entry> set = map.entrySet();
        for (Map.Entry entry : set) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (null != value) {
                String property = null;
                if (key instanceof Enum) {
                    property = ((Enum) key).name();
                } else {
                    property = key.toString();
                }
                if (isFirst) {
                    isFirst = false;
                } else {
                    buffer.append(',');
                }
                buffer.append('\"');
                buffer.append(property);
                buffer.append("\":");
                buffer.append(OBJECT_JSON_WRITER.write(value));
            }
        }
        buffer.append('}');
    }
}
