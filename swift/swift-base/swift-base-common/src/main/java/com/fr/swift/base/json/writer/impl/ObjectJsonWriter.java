package com.fr.swift.base.json.writer.impl;

import com.fr.swift.base.json.serialization.SerializationConfig;
import com.fr.swift.base.json.serialization.SerializationConfigHolder;
import com.fr.swift.base.json.writer.JsonWriter;
import com.fr.swift.util.ReflectUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author yee
 * @date 2018-12-12
 */
public class ObjectJsonWriter implements JsonWriter {
    private static final CollectionJsonWriter COLLECTION_JSON_WRITER = new CollectionJsonWriter();
    private static final MapJsonWriter MAP_JSON_WRITER = new MapJsonWriter();
    public static final String D_QUOTA_REPLACEMENT = "__D_QUOTA__";
    public static final String S_QUOTA_REPLACEMENT = "__S_QUOTA__";

    @Override
    public String write(Object o) {
        if (o instanceof Date) {
            return String.valueOf(((Date) o).getTime());
        }
        Class reference = o.getClass();
        if (ReflectUtils.isPrimitiveOrWrapper(reference)) {
            return o.toString();
        }
        if (o instanceof String) {
            String tmp = (String) o;
            tmp = tmp.replace("'", S_QUOTA_REPLACEMENT);
            return String.format("\"%s\"", tmp.replace("\"", D_QUOTA_REPLACEMENT));
        }
        if (o instanceof Enum) {
            return String.format("\"%s\"", ((Enum) o).name());
        }
        if (o instanceof Collection) {
            return COLLECTION_JSON_WRITER.write((Collection) o);
        }
        if (o instanceof Map) {
            return MAP_JSON_WRITER.write((Map) o);
        }
        StringBuffer buffer = new StringBuffer(1024);
        if (reference.isArray()) {
            writeArray(buffer, o);
        } else {
            writeObject(buffer, o);
        }
        return buffer.toString();
    }

    private void writeArray(StringBuffer buffer, Object array) {
        if (array instanceof boolean[]) {
            buffer.append(Arrays.toString((boolean[]) array));
        } else if (array instanceof int[]) {
            buffer.append(Arrays.toString((int[]) array));
        } else if (array instanceof long[]) {
            buffer.append(Arrays.toString((long[]) array));
        } else if (array instanceof float[]) {
            buffer.append(Arrays.toString((float[]) array));
        } else if (array instanceof double[]) {
            buffer.append(Arrays.toString((double[]) array));
        } else if (array instanceof short[]) {
            buffer.append(Arrays.toString((short[]) array));
        } else if (array instanceof byte[]) {
            buffer.append(Arrays.toString((byte[]) array));
        } else {
            buffer.append(COLLECTION_JSON_WRITER.write(Arrays.asList((Object[]) array)));
        }
    }

    private void writeObject(StringBuffer buffer, Object bean) {
        if (bean == null) {
            buffer.append("null");
            return;
        }
        Class<? extends Object> beanClass = bean.getClass();
        SerializationConfig config = SerializationConfigHolder.getInstance().getSerializationConfig(beanClass);
        Map<String, SerializationConfig.BeanGetter> getters = config.getters();
        if (getters.isEmpty()) {
            buffer.append("{}");
            return;
        }
        buffer.append('{');
        boolean isFirst = true;
        boolean isNull = false;
        for (String propertyName : getters.keySet()) {
            if (isFirst) {
                isFirst = false;
            } else if (!isNull) {
                buffer.append(',');
            }
            SerializationConfig.BeanGetter pg = getters.get(propertyName);
            try {
                Object obj = pg.get(bean);
                if (null != obj) {
                    buffer.append('\"');
                    buffer.append(propertyName);
                    buffer.append("\":");
                    write(buffer, obj);
                    isNull = false;
                } else {
                    isNull = true;
                }
            } catch (Exception ignore) {
            }

        }
        if (buffer.toString().endsWith(",")) {
            buffer.setLength(buffer.length() - 1);
        }
        buffer.append('}');
    }

    private void write(StringBuffer buffer, Object obj) {
        if (obj == null) {
            buffer.append("null");
            return;
        }
        if (obj instanceof Collection) {
            buffer.append(COLLECTION_JSON_WRITER.write((Collection) obj));
            return;
        }
        if (obj instanceof Date) {
            buffer.append(((Date) obj).getTime());
            return;
        }
        Class reference = obj.getClass();
        if (ReflectUtils.isPrimitiveOrWrapper(reference)) {
            buffer.append(obj.toString());
            return;
        }
        if (obj instanceof String) {
            String tmp = (String) obj;
            tmp = tmp.replace("'", S_QUOTA_REPLACEMENT);
            buffer.append(String.format("\"%s\"", tmp.replace("\"", D_QUOTA_REPLACEMENT)));
            return;
        }
        if (reference.isEnum()) {
            buffer.append(String.format("\"%s\"", ((Enum) obj).name()));
            return;
        }
        if (obj instanceof Map) {
            buffer.append(MAP_JSON_WRITER.write((Map) obj));
            return;
        }
        if (reference.isArray()) {
            writeArray(buffer, obj);
            return;
        }
        writeObject(buffer, obj);
    }
}
