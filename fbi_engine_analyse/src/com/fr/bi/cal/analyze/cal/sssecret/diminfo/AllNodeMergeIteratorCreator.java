package com.fr.bi.cal.analyze.cal.sssecret.diminfo;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.cal.analyze.cal.sssecret.mergeiter.AllNodeMergeIterator;
import com.fr.bi.cal.analyze.cal.sssecret.mergeiter.MergeIterator;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.stable.engine.cal.DimensionIterator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.general.NameObject;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/1/12.
 */
public class AllNodeMergeIteratorCreator implements MergeIteratorCreator {
    private DimensionFilter filter;
    private NameObject targetSort;
    private List<TargetAndKey>[] metricsToCalculate;
    private Map<String, TargetCalculator> calculatedMap;
    private MergeIteratorCreator creator;
    private BIMultiThreadExecutor executor;
    private List<CalCalculator> calCalculator;

    public AllNodeMergeIteratorCreator(DimensionFilter filter, NameObject targetSort, List<TargetAndKey>[] metricsToCalculate, Map<String, TargetCalculator> calculatedMap, MergeIteratorCreator creator, BIMultiThreadExecutor executor, List<CalCalculator> calCalculator) {
        this.filter = filter;
        this.targetSort = targetSort;
        this.metricsToCalculate = metricsToCalculate;
        this.calculatedMap = calculatedMap;
        this.creator = creator;
        this.executor = executor;
        this.calCalculator = calCalculator;
    }

    @Override
    public Iterator<MetricMergeResult> createIterator(DimensionIterator[] iterators, int sumLength, GroupValueIndex[] gvis, Comparator c, ICubeTableService[] tis, ICubeDataLoader loader) {
        MergeIterator iterator = (MergeIterator) creator.createIterator(iterators, sumLength, gvis, c, tis, loader);
        return new AllNodeMergeIterator(iterator, sumLength, filter, targetSort, metricsToCalculate, calculatedMap, tis, loader, executor, calCalculator);
    }

    @Override
    public void setExecutor(BIMultiThreadExecutor executor) {
        this.executor = executor;
    }
}
