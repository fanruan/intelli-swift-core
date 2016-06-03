package com.fr.bi.stable.structure.collection.map;

import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.finebi.cube.api.ICubeColumnIndexReader;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class CubeTreeMap<K> extends TreeMap implements ICubeColumnIndexReader, Release {

    /**
     *
     */
    private static final long serialVersionUID = -785392454795497925L;


    public CubeTreeMap(Comparator comparator) {
        super(comparator);
    }

    @Override
    public int sizeOfGroup() {
        return 0;
    }

    @Override
    public GroupValueIndex[] getGroupIndex(Object[] keys) {
        java.util.List<GroupValueIndex> list = new java.util.ArrayList();
        for (int i = 0; i < keys.length; i++) {
            list.add((GroupValueIndex) get(keys[i]));
        }

        return list.toArray(new GroupValueIndex[list.size()]);
    }

    /**
     * key数组
     *
     * @param length 长度
     * @return key 数组
     */
    @Override
    public Object[] createKey(int length) {
        return (K[])new Object[length];
    }

    @Override
    public Object createValue(Object v) {
        return v;
    }

    @Override
    public Iterator iterator() {
        return entrySet().iterator();
    }

    @Override
    public Iterator<Map.Entry> previousIterator() {
        return descendingMap().entrySet().iterator();
    }

    /**
     * 从某个值开始的entry的set
     *
     * @param start
     * @return entry的set
     */
    @Override
    public Iterator<Map.Entry> iterator(Object start) {
        return new IteratorFromStart(iterator(), start);
    }

    /**
     * 从某个值开始的反向的entry的set
     *
     * @param start
     * @return entry的set
     */
    @Override
    public Iterator<Map.Entry> previousIterator(Object start) {
        return new IteratorFromStart(previousIterator(), start);
    }

    @Override
    public Object getOriginalValue(int rowNumber) {
        return null;
    }

    @Override
    public long nonPrecisionSize() {
        return size();
    }

    @Override
    public Object getGroupValue(int position) {
        return null;
    }

    @Override
    public int getClassType() {
        return 0;
    }
}