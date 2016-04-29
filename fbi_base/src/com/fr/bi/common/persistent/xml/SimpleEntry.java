package com.fr.bi.common.persistent.xml;

/**
 * Created by Connery on 2016/1/3.
 */
public class SimpleEntry {
    public Object key;
    public Object value;

    public SimpleEntry(Object key, Object value) {
        this.value = value;
        this.key = key;
    }

    public SimpleEntry() {
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}