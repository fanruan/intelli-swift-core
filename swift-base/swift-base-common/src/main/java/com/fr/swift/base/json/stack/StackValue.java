package com.fr.swift.base.json.stack;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018-12-12
 */
public class StackValue {

    public static final int TYPE_OBJECT = 0;

    public static final int TYPE_OBJECT_KEY = 1;

    public static final int TYPE_ARRAY = 2;

    public static final int TYPE_SINGLE = 3;

    private final int type;
    private final Object value;

    private StackValue(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static StackValue newJsonObject(Map<String, Object> map) {
        return new StackValue(TYPE_OBJECT, map);
    }

    public static StackValue newJsonObjectKey(String key) {
        return new StackValue(TYPE_OBJECT_KEY, key);
    }

    public static StackValue newJsonArray(List<Object> arr) {
        return new StackValue(TYPE_ARRAY, arr);
    }

    public static StackValue newJsonSingle(Object obj) {
        return new StackValue(TYPE_SINGLE, obj);
    }

    public String valueAsKey() {
        return (String) value;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> valueAsObject() {
        return (Map<String, Object>) value;
    }

    @SuppressWarnings("unchecked")
    public List<Object> valueAsArray() {
        return (List<Object>) value;
    }

    public Object getValue() {
        return value;
    }

    public int getType() {
        return type;
    }
}
