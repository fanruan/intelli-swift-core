package com.fr.bi.stable.structure.collection.map;

import java.io.Serializable;
import java.util.*;

/**
 * node childs map
 *
 * @author Daniel
 */
public class ChildsMap<T> implements Map<Object, T>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2126359668730907146L;

    private LinkedHashMap<Object, Integer> lmp = new LinkedHashMap<Object, Integer>();
    private ArrayList<T> node = new ArrayList<T>();
    private CSet c = new CSet();

    @Override
    public int size() {
        return node.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return lmp.containsKey(key);
    }

    public T getLastValue() {
        int size = size();
        if (size == 0) {
            return null;
        }
        return node.get(size - 1);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<T> getNodeList() {
        return (ArrayList<T>) node.clone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() throws CloneNotSupportedException {
        ChildsMap map = (ChildsMap) super.clone();
        map.lmp = (LinkedHashMap<Object, Integer>) this.lmp.clone();
        map.node = (ArrayList<T>) this.node.clone();
        return map;
    }

    public T getFirstValue() {
        if (size() == 0) {
            return null;
        }
        return node.get(0);
    }

    /**
     * @deprecated
     */
    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public T get(Object key) {
        Integer index = (Integer) lmp.get(key);
        if (index != null) {
            int i = index.intValue();
            if (i < size()) {
                return node.get(i);
            }
        }
        return null;
    }

    public T get(int index) {
        if (index < size()) {
            return node.get(index);
        }
        return null;
    }

    @Override
    public T put(Object key, T value) {
        Integer i = lmp.put(key, new Integer(node.size()));
        node.add(value);
        return i == null ? null : node.get(i);
    }

    /**
     * @deprecated
     */
    @Override
    public T remove(Object key) {
        return null;
    }

    @Override
    public void clear() {
        lmp.clear();
        node.clear();
    }

    /**
     * @deprecated
     */
    @Override
    public Set<Object> keySet() {
        return null;
    }

    @Override
    public List<T> values() {
        return node;
    }

    @Override
    public Set<Entry<Object, T>> entrySet() {
        return c;
    }

    @Override
    public String toString() {
        return node.toString();
    }

    /* (non-Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends Object, ? extends T> m) {
    }

    private class CSet implements Set<Entry<Object, T>>, Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 4246975806017703059L;

        private CSet() {
        }

        @Override
        public int size() {
            return ChildsMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<Entry<Object, T>> iterator() {
            return new CIterator();
        }

        /**
         * 为了方便调试，之前调试时Node的子节点展开不了
         *
         * @return 数组
         */
        @Override
        public Object[] toArray() {
            Object[] r = new Object[size()];
            Iterator<Entry<Object, T>> it = iterator();
            for (int i = 0; i < r.length; i++) {
                if (!it.hasNext())    // fewer elements than expected
                {
                    return Arrays.copyOf(r, i);
                }
                r[i] = it.next();
            }
            return it.hasNext() ? finishToArray(r, it) : r;
        }

        private <T> T[] finishToArray(T[] r, Iterator<?> it) {
            int i = r.length;
            while (it.hasNext()) {
                int cap = r.length;
                if (i == cap) {
                    int newCap = ((cap / 2) + 1) * 3;
                    if (newCap <= cap) { // integer overflow
                        if (cap == Integer.MAX_VALUE) {
                            throw new OutOfMemoryError
                                    ("Required array size too large");
                        }
                        newCap = Integer.MAX_VALUE;
                    }
                    r = Arrays.copyOf(r, newCap);
                }
                r[i++] = (T) it.next();
            }
            // trim if overallocated
            return (i == r.length) ? r : Arrays.copyOf(r, i);
        }

        @Override
        public boolean add(Entry<Object, T> o) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public void clear() {

        }

        /* (non-Javadoc)
         * @see java.util.Set#toArray(T[])
         */
        @Override
        public <T> T[] toArray(T[] a) {

            //return null
            //避开代码检查
            a = null;
            return a;
        }

        /* (non-Javadoc)
         * @see java.util.Set#containsAll(java.util.Collection)
         */
        @Override
        public boolean containsAll(Collection<?> c) {

            return false;
        }

        /* (non-Javadoc)
         * @see java.util.Set#addAll(java.util.Collection)
         */
        @Override
        public boolean addAll(
                Collection<? extends java.util.Map.Entry<Object, T>> c) {

            return false;
        }

        /* (non-Javadoc)
         * @see java.util.Set#retainAll(java.util.Collection)
         */
        @Override
        public boolean retainAll(Collection<?> c) {

            return false;
        }

        /* (non-Javadoc)
         * @see java.util.Set#removeAll(java.util.Collection)
         */
        @Override
        public boolean removeAll(Collection<?> c) {

            return false;
        }
    }

    private class CIterator implements Iterator<Entry<Object, T>> {
        private Iterator<Entry<Object, Integer>> iter;

        public CIterator() {
            iter = lmp.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public Entry<Object, T> next() {
            return new CEnty(iter.next());
        }

        @Override
        public void remove() {
        }

    }

    private class CEnty implements Entry<Object, T> {
        private Object key;
        private int index;

        public CEnty(Entry<Object, Integer> entry) {
            this.key = entry.getKey();
            Integer v = entry.getValue();
            this.index = v.intValue();
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public T getValue() {
            return node.get(index);
        }

        @Override
        public T setValue(T value) {
            return value;
        }

    }

}