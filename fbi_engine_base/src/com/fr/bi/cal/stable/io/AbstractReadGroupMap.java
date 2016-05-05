package com.fr.bi.cal.stable.io;

import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;
import com.finebi.cube.api.ICubeColumnIndexReader;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by GUY on 2015/4/10.
 */
public abstract class AbstractReadGroupMap<T> implements ICubeColumnIndexReader<T>, Release {
 
    protected ISortNIOReadList<T> lmp;
    protected ICubeTableIndexReader indexes;
    private CSet<Map.Entry<T, GroupValueIndex>> ascSet = new CSet<Map.Entry<T, GroupValueIndex>>(true);
    private CSet<Map.Entry<T, GroupValueIndex>> descSet = new CSet<Map.Entry<T, GroupValueIndex>>(false);

    protected AbstractReadGroupMap() {
    }

    protected AbstractReadGroupMap(ISortNIOReadList<T> lmp, ICubeTableIndexReader indexes) {
        this.lmp = lmp;
        this.indexes = indexes;
    }

    @Override
    public GroupValueIndex[] getGroupIndex(T[] keys) {
        int[] indices = lmp.indexOf(keys);
        GroupValueIndex[] res = new GroupValueIndex[keys.length];
        for (int i = 0; i < indices.length; i++) {
            res[i] = (indices[i] != -1 ? indexes.get(indices[i]) : null);
        }
        return res;
    }

    @Override
    public int sizeOfGroup() {
        return 0;
    }

    private T getKey(int keyIndex) {
        return lmp.get(keyIndex);
    }

    private GroupValueIndex getGroupValueIndex(int keyIndex) {
        return indexes.get(keyIndex);
    }

    @Override
    public Iterator<Map.Entry<T, GroupValueIndex>> iterator() {
        return ascSet.iterator();
    }

    @Override
    public Iterator<Map.Entry<T, GroupValueIndex>> previousIterator() {
        return descSet.iterator();
    }

    /**
     * 从某个值开始的entry的set
     *
     * @param start
     * @return entry的set
     */
    @Override
    public Iterator<Map.Entry<T, GroupValueIndex>> iterator(T start) {
        return ascSet.iterator(start);
    }

    /**
     * 从某个值开始的反向的entry的set
     *
     * @param start
     * @return entry的set
     */
    @Override
    public Iterator<Map.Entry<T, GroupValueIndex>> previousIterator(T start) {
        return descSet.iterator(start);
    }

    public int size() {
        return lmp.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public void clear() {
        if (lmp != null) {
            lmp.clear();
        }
        if (indexes != null) {
            indexes.clear();
        }
    }

    @Override
    public T[] createKey(int length) {
        return lmp.createKey(length);
    }

    @Override
    public T createValue(Object v) {
        return lmp.createValue(v);
    }

    @Override
    public T getGroupValue(int position) {
        return null;
    }

    private class CSet<V> implements Set<V>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 4997766422304383192L;
        private boolean asc;

        private CSet(boolean asc) {
            this.asc = asc;
        }

        @Override
        public int size() {
            return AbstractReadGroupMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return AbstractReadGroupMap.this.size() == 0;
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Integer) {
                Integer value = (Integer) o;
                T[] g = createKey(1);
                g[0] = (T) value;
                return lmp.indexOf(g)[0] > -1;
            } else if (o instanceof Number) {
                Number value = ((Number) o).doubleValue();
                T[] g = createKey(1);
                g[0] = (T) value;
                return lmp.indexOf(g)[0] > -1;
            }
            return false;
        }

        // august:这儿必须重新new啊 不然第二次用迭代器的时候 不久出错了吗
        @Override
        public Iterator<V> iterator() {
            if (asc) {
                return new CIterator();
            } else {
                return new DIterator();
            }
        }
        public Iterator<V> iterator(T start) {
            if (asc) {
                return new CIterator(start);
            } else {
                return new DIterator(start);
            }
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public Object[] toArray(Object[] a) {
            return new Object[0];
        }

        @Override
        public boolean add(Object o) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection c) {
            return false;
        }

        @Override
        public boolean addAll(Collection c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection c) {
            return false;
        }

        @Override
        public void clear() {

        }
    }

    private class DIterator implements Iterator {
        private int c_index;

        public DIterator() {
            c_index = AbstractReadGroupMap.this.size();
        }

        public DIterator(T start) {
            T[] keys = createKey(1);
            keys[0] = start;
            c_index = AbstractReadGroupMap.this.size() - lmp.indexOf(keys)[0];
        }

        @Override
        public boolean hasNext() {
            return c_index > 0;
        }

        @Override
        public Object next() {
            return new CEnty(--c_index);
        }

        @Override
        public void remove() {
        }


    }

    private class CIterator implements Iterator {
        private int c_index;

        public CIterator() {
            c_index = 0;
        }

        public CIterator(T start) {
            T[] keys = createKey(1);
            keys[0] = start;
            c_index = lmp.indexOf(keys)[0];
        }

        @Override
        public boolean hasNext() {
            return c_index < AbstractReadGroupMap.this.size();
        }

        @Override
        public Object next() {
            return new CEnty(c_index++);
        }

        @Override
        public void remove() {
        }

    }

    private class CEnty implements Map.Entry {
        private int index;

        public CEnty(int index) {
            this.index = index;
        }

        @Override
        public Object getKey() {
            return AbstractReadGroupMap.this.getKey(index);
        }

        @Override
        public Object getValue() {
            return AbstractReadGroupMap.this.getGroupValueIndex(index);
        }

        @Override
        public Object setValue(Object value) {
            return null;
        }

    }
    
    @Override
	public T firstKey() {
    	return getKey(0);
    }

    @Override
	public T lastKey() {
    	return getKey(size() - 1);
    }
    
	@Override
	public long nonPrecisionSize() {
		return size();
	}
}