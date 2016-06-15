package com.fr.bi.stable.structure.collection.map;

import com.fr.bi.stable.gvi.GroupValueIndex;
import com.finebi.cube.api.ICubeColumnIndexReader;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class CubeLinkedHashMap extends LinkedHashMap implements
        ICubeColumnIndexReader {

    /**
     *
     */
    private static final long serialVersionUID = 3909440186382050281L;

    @Override
    public Object getGroupValue(int position) {
        return null;
    }

    @Override
    public GroupValueIndex getNULLIndex() {
        return null;
    }

    @Override
    public Object getOriginalValue(int rowNumber) {
        return null;
    }

    @Override
    public int sizeOfGroup() {
        return size();
    }

    @Override
    public GroupValueIndex[] getGroupIndex(Object[] keys) {
        java.util.List<GroupValueIndex> list = new java.util.ArrayList<GroupValueIndex>();
        for (int i = 0; i < keys.length; i++) {
            list.add(getIndex(keys[i]));
        }

        return list.toArray(new GroupValueIndex[list.size()]);
    }

    public GroupValueIndex getIndex(Object key) {
        return (GroupValueIndex)get(key);
    }

    @Override
    public Object[] createKey(int length) {
        return new Object[length];
    }

    /**
     * 获取key值
     *
     * @param keyIndex key顺序的第几个
     * @return
     */
    private Object getKey(int keyIndex) {
        Iterator it = keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Object o = it.next();
            if (i == keyIndex) {
                return o;
            }
            i++;
        }
        return null;
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
	public Iterator previousIterator() {
		throw new UnsupportedOperationException();
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
	public Object firstKey() {
		return getKey(0);
	}

	@Override
	public Object lastKey() {
		return  getKey(size() - 1);
	}

	@Override
	public long nonPrecisionSize() {
		return size();
	}


}