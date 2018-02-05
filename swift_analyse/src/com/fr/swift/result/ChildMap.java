package com.fr.swift.result;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * node childs map
 *
 * @author Daniel
 */
public class ChildMap<T> {

    /**
     *
     */
    private static final long serialVersionUID = -2126359668730907146L;

    private LinkedHashMap<Object, Integer> lmp = new LinkedHashMap<Object, Integer>();
    private ArrayList<T> list = new ArrayList<T>();

    public int size() {
        return list.size();
    }


    @Override
    @SuppressWarnings("unchecked")
    public Object clone() throws CloneNotSupportedException {
        ChildMap map = (ChildMap) super.clone();
        map.lmp = (LinkedHashMap<Object, Integer>) this.lmp.clone();
        map.list = (ArrayList<T>) this.list.clone();
        return map;
    }


    public T get(Object key) {
        Integer index =  lmp.get(key);
        if (index != null) {
            int i = index.intValue();
            if (i < size()) {
                return list.get(i);
            }
        }
        return null;
    }

    public T get(int index) {
        if (index < size()) {
            return list.get(index);
        }
        return null;
    }

    public T put(Object key, T value) {
        Integer i = lmp.put(key, new Integer(list.size()));
        list.add(value);
        return i == null ? null : list.get(i);
    }

    public void clear() {
        lmp.clear();
        list.clear();
    }

    @Override
    public String toString() {
        return list.toString();
    }


    public int getIndex(Object key) {
        return lmp.get(key);
    }
}