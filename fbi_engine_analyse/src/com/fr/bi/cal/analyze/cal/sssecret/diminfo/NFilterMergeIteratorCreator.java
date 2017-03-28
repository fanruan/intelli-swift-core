package com.fr.bi.cal.analyze.cal.sssecret.diminfo;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.cal.analyze.cal.sssecret.mergeiter.NFilterMergeIterator;
import com.fr.bi.stable.engine.cal.DimensionIterator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by 小灰灰 on 2017/1/12.
 */
public class NFilterMergeIteratorCreator implements MergeIteratorCreator {
    private int count;

    public NFilterMergeIteratorCreator(int count) {
        this.count = count;
    }

    @Override
    public Iterator<MetricMergeResult> createIterator(DimensionIterator[] iterators, GroupValueIndex[] gvis, Comparator c, ICubeTableService[] tis, ICubeDataLoader loader) {
        return new NFilterMergeIterator(iterators, gvis, c, count);
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}