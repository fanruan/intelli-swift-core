package com.fr.bi.cal.analyze.cal.sssecret.mergeiter;

import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.stable.collections.array.IntArray;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2016/12/30.
 */
public class MergeIterator implements Iterator<MetricMergeResult>{
    private Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators;
    private GroupValueIndex[] gvis;
    private Comparator c;
    private Map.Entry<Object, GroupValueIndex>[] entries;
    protected MetricMergeResult next;

    public MergeIterator(Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators, GroupValueIndex[] gvis, Comparator c) {
        this.iterators = iterators;
        this.gvis = gvis;
        this.c = c;
        initEntries();
    }



    @Override
    public boolean hasNext() {
        moveNext();
        return next != null;
    }

    @Override
    public MetricMergeResult next() {
        return next;
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
        GroupValueIndex[] gvis = new GroupValueIndex[iterators.length];
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
            for (int i = 0; i < gvis.length ; i++){
                if (this.gvis[i] == null || gvis[i] == null){
                    gvis[i] = null;
                } else {
                    gvis[i] = gvis[i].AND(this.gvis[i]);
                }
            }
            next = new MetricMergeResult(c, minValue, gvis);
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
