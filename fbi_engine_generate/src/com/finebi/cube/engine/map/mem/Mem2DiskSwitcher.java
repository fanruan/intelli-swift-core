package com.finebi.cube.engine.map.mem;

import com.finebi.cube.engine.map.ExternalMap;

import java.util.*;

/**
 * Created by FineSoft on 2015/7/16.
 */
public abstract class Mem2DiskSwitcher<K, V> implements Map<K, V> {
    private TreeMap mem;
    private ExternalMap disk;
    private int switchSign = 0;
    private int count = 0;

    public Mem2DiskSwitcher(TreeMap mem, ExternalMap disk) {
        this.mem = mem;
        this.disk = disk;
        this.switchSign = 0;
    }


    abstract long getThreshold();

    private Map<K, V> fetchMap() {

        if (switchSign == 0) {
            return mem;
        } else {
            return disk;
        }
    }

    public boolean isDisk() {
        return switchSign == 1;
    }

    private void checkSwitcher() {

        if (switchSign == 0) {
            if (mem.size() > getThreshold()) {
                disk.dump(mem);
                switchSign = 1;
            }

        }
    }

    @Override
	public int size() {
        return fetchMap().size();
    }

    @Override
	public boolean isEmpty() {
        return fetchMap().isEmpty();
    }

    @Override
	public boolean containsKey(Object key) {
        return fetchMap().containsKey(key);
    }

    @Override
	public boolean containsValue(Object value) {
        return fetchMap().containsKey(value);
    }

    @Override
	public V get(Object key) {
        return fetchMap().get(key);
    }

    @Override
	public V put(K key, V value) {
        checkSwitcher();
        return fetchMap().put(key, value);
    }

    @Override
	public V remove(Object key) {
        return fetchMap().remove(key);
    }

    @Override
	public void putAll(Map<? extends K, ? extends V> m) {
        fetchMap().putAll(m);
    }

    @Override
	public void clear() {
        fetchMap().clear();
    }

    @Override
	public Set<K> keySet() {
        return fetchMap().keySet();
    }

    @Override
	public Collection<V> values() {
        return fetchMap().values();
    }

    @Override
	public Set<Entry<K, V>> entrySet() {
        return fetchMap().entrySet();
    }

    public Iterator<Entry<K, V>> getIterator() {
        if (switchSign == 0) {
            return mem.entrySet().iterator();
        } else {
            return disk.getIterator();
        }
    }

    public void release() {
        if (switchSign == 1) {
            disk.release();
        }
    }
}