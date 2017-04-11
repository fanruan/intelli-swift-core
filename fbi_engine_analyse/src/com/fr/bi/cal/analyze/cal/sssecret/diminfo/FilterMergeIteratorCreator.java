package com.fr.bi.cal.analyze.cal.sssecret.diminfo;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.cal.analyze.cal.sssecret.mergeiter.FilterMergeIterator;
import com.fr.bi.field.filtervalue.string.onevaluefilter.StringOneValueFilterValue;
import com.fr.bi.stable.engine.cal.DimensionIterator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by 小灰灰 on 2017/1/12.
 */
public class FilterMergeIteratorCreator implements MergeIteratorCreator {
    private StringOneValueFilterValue filterValue;

    public FilterMergeIteratorCreator(StringOneValueFilterValue filterValue) {
        this.filterValue = filterValue;
    }

    @Override
    public Iterator<MetricMergeResult> createIterator(DimensionIterator[] iterators, int sumLength, GroupValueIndex[] gvis, Comparator c, ICubeTableService[] tis, ICubeDataLoader loader) {
        return new FilterMergeIterator(iterators, gvis, c, sumLength, filterValue);
    }

    @Override
    public void setExecutor(BIMultiThreadExecutor executor) {

    }
}
