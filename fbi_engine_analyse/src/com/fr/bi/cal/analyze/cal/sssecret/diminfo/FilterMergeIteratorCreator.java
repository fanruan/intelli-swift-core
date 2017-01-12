package com.fr.bi.cal.analyze.cal.sssecret.diminfo;

import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/1/12.
 */
public class FilterMergeIteratorCreator implements MergeIteratorCreator {
    @Override
    public Iterator<MetricMergeResult> createIterator(Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators, GroupValueIndex[] gvis, Comparator c) {
        return null;
    }
}
