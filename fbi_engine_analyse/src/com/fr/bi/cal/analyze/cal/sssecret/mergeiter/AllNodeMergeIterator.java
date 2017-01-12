package com.fr.bi.cal.analyze.cal.sssecret.mergeiter;

import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/1/9.
 */
public class AllNodeMergeIterator extends MergeIterator {
    public AllNodeMergeIterator(Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators, GroupValueIndex[] gvis, Comparator c) {
        super(iterators, gvis, c);
    }
}
