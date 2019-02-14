package com.fr.swift.task;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/8/6
 */
public class ReadyUploadContainer implements Map<TaskKey, Object> {
    private static ReadyUploadContainer container;
    private ConcurrentHashMap<TaskKey, Object> map;

    private ReadyUploadContainer() {
        this.map = new ConcurrentHashMap<TaskKey, Object>();
    }

    public static ReadyUploadContainer instance() {
        if (null == container) {
            synchronized (ReadyUploadContainer.class) {
                if (null == container) {
                    container = new ReadyUploadContainer();
                }
            }
        }
        return container;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(TaskKey key, Object value) {
        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends TaskKey, ?> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<TaskKey> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Entry<TaskKey, Object>> entrySet() {
        return map.entrySet();
    }
}
