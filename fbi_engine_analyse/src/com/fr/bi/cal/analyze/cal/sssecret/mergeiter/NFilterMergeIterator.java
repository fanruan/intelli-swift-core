package com.fr.bi.cal.analyze.cal.sssecret.mergeiter;

import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/1/11.
 */
public class NFilterMergeIterator extends MergeIterator {
    private int n = 0;
    private int count = 0;

    public NFilterMergeIterator(Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators, GroupValueIndex[] gvis, Comparator c, int n) {
        super(iterators, gvis, c);
        this.n = n;
    }

    @Override
    protected void moveNext() {
        super.moveNext();
        if (count >= n) {
            next = null;
            return;
        }
        count++;
    }
}
