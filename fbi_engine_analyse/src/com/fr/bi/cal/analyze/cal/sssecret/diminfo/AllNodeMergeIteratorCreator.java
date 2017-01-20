package com.fr.bi.cal.analyze.cal.sssecret.diminfo;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.cal.analyze.cal.sssecret.mergeiter.AllNodeMergeIterator;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
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

    public AllNodeMergeIteratorCreator(DimensionFilter filter, NameObject targetSort, List<TargetAndKey>[] metricsToCalculate, Map<String, TargetCalculator> calculatedMap, MergeIteratorCreator creator) {
        this.filter = filter;
        this.targetSort = targetSort;
        this.metricsToCalculate = metricsToCalculate;
        this.calculatedMap = calculatedMap;
        this.creator = creator;
    }

    public Map<String, TargetCalculator> getCalculatedMap() {
        return calculatedMap;
    }

    @Override
    public Iterator<MetricMergeResult> createIterator(Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators, GroupValueIndex[] gvis, Comparator c, ICubeTableService[] tis, ICubeDataLoader loader) {
        Iterator<MetricMergeResult> iterator = creator.createIterator(iterators, gvis, c, tis, loader);
        return new AllNodeMergeIterator(iterator, filter, targetSort, metricsToCalculate, calculatedMap, tis, loader);
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
