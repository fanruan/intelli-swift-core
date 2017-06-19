package com.fr.bi.cal.analyze.cal.sssecret.mergeiter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.exception.BIMemoryDataOutOfLimitException;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.general.NameObject;

import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/6/13.
 */
public class LimitedAllNodeMergeIterator extends AllNodeMergeIterator {
    private int maxSize;
    public LimitedAllNodeMergeIterator(MergeIterator mergeIterator, int sumLength, DimensionFilter filter, NameObject targetSort, List<TargetAndKey>[] metricsToCalculate, Map<String, TargetCalculator> calculatedMap, ICubeTableService[] tis, ICubeDataLoader loader, BIMultiThreadExecutor executor, List<CalCalculator> formulaCalculator, int maxSize) {
        super(mergeIterator, sumLength, filter, targetSort, metricsToCalculate, calculatedMap, tis, loader, executor, formulaCalculator);
        this.maxSize = maxSize;
        initIterLater(sumLength);
    }

    @Override
    protected void initIter(int sumLength) {

    }

    //要先设置下maxSize
    protected void initIterLater(int sumLength) {
        super.initIter(sumLength);
    }

    protected void initRoot() {
        while (mergeIterator.hasNext()) {
            MetricMergeResult result = mergeIterator.next();
            checkSum(result);
            root.addChild(result);
            if (size > maxSize){
                throw new BIMemoryDataOutOfLimitException();
            }
            ++size;
        }
    }
}
