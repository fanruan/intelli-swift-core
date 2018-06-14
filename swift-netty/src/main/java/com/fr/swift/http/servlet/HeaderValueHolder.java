package com.fr.swift.http.servlet;

import com.fr.third.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/6/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
class HeaderValueHolder {

    private final List<Object> values = new LinkedList<Object>();


    public void setValue(Object value) {
        this.values.clear();
        this.values.add(value);
    }

    public void addValue(Object value) {
        this.values.add(value);
    }

    public void addValues(Collection<?> values) {
        this.values.addAll(values);
    }

    public void addValueArray(Object values) {
        CollectionUtils.mergeArrayIntoCollection(values, this.values);
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(this.values);
    }

    public List<String> getStringValues() {
        List<String> stringList = new ArrayList<String>(this.values.size());
        for (Object value : this.values) {
            stringList.add(value.toString());
        }
        return Collections.unmodifiableList(stringList);
    }

    public Object getValue() {
        return (!this.values.isEmpty() ? this.values.get(0) : null);
    }

    public String getStringValue() {
        return (!this.values.isEmpty() ? this.values.get(0).toString() : null);
    }


    /**
     * Find a HeaderValueHolder by name, ignoring casing.
     *
     * @param headers the Map of header names to HeaderValueHolders
     * @param name    the name of the desired header
     * @return the corresponding HeaderValueHolder,
     * or <code>null</code> if none found
     */
    public static HeaderValueHolder getByName(Map<String, HeaderValueHolder> headers, String name) {
        for (String headerName : headers.keySet()) {
            if (headerName.equalsIgnoreCase(name)) {
                return headers.get(headerName);
            }
        }
        return null;
    }

}
