package com.fr.bi.cal.analyze.cal.sssecret.mergeiter;

import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.stable.collections.array.IntArray;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2016/12/30.
 */
public class MergeIterator implements Iterator<Map.Entry<Object, GroupValueIndex[]>>{
    private Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators;
    private Comparator c;
    private Map.Entry<Object, GroupValueIndex>[] entries;
    protected Map.Entry<Object, GroupValueIndex[]> next;

    public MergeIterator(Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators, Comparator c) {
        this.iterators = iterators;
        this.c = c;
        initEntries();
        moveNext();
    }



    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Map.Entry<Object, GroupValueIndex[]> next() {
        Map.Entry<Object, GroupValueIndex[]> temp = next;
        moveNext();
        return temp;
    }

    @Override
    public void remove() {

    }

    private void initEntries() {
        entries = new Map.Entry[iterators.length];
        for (int i = 0; i < entries.length; i++){
            if (iterators[i].hasNext()){
                entries[i] = iterators[i].next();
            }
        }
    }

    private void moveEntries(IntArray array) {
        for (int i = 0; i < array.size; i++){
            int index = array.get(i);
            if (iterators[index].hasNext()){
                entries[index] = iterators[index].next();
            } else {
                entries[index] = null;
            }
        }
    }

    protected void moveNext() {
        Object minValue = null;
        IntArray array = new IntArray();
        final GroupValueIndex[] gvis = new GroupValueIndex[iterators.length];
        for (int i = 0; i < entries.length; i++) {
            Map.Entry<Object, GroupValueIndex> entry = entries[i];
            if (entry != null) {
                Object currentValue = entry.getKey();
                if (minValue == null) {
                    minValue = currentValue;
                    gvis[i] = entry.getValue();
                    array.add(i);
                } else {
                    int result = c.compare(minValue, currentValue);
                    if (result == 0){
                        gvis[i] = entry.getValue();
                        array.add(i);
                    }
                    if (result > 0) {
                        minValue = currentValue;
                        Arrays.fill(gvis, null);
                        array.clear();
                        array.add(i);
                        gvis[i] = entry.getValue();
                    }
                }
            }
        }
        if (minValue == null){
            next = null;
        } else {
            final Object finalMinValue = minValue;
            next = new Map.Entry<Object, GroupValueIndex[]>() {
                @Override
                public Object getKey() {
                    return finalMinValue;
                }

                @Override
                public GroupValueIndex[] getValue() {
                    return gvis;
                }

                @Override
                public GroupValueIndex[] setValue(GroupValueIndex[] value) {
                    return new GroupValueIndex[0];
                }
            };
        }
        moveEntries(array);
    }

    public static Iterator EMPTY = new Iterator() {
        @Override
        public void remove() {

        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }
    };
}
