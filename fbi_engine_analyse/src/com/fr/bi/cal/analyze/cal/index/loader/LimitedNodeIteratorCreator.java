package com.fr.bi.cal.analyze.cal.index.loader;

import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.result.NodeCreator;
import com.fr.bi.cal.analyze.cal.sssecret.ConstructedRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.PartConstructedRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.LimitedAllNodeMergeIteratorCreator;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.general.NameObject;

import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/6/13.
 */
public class LimitedNodeIteratorCreator extends NodeIteratorCreator {
    private int maxSize;

    public LimitedNodeIteratorCreator(List<MetricGroupInfo> metricGroupInfoList, BIDimension[] rowDimension, BISummaryTarget[] usedTargets, int sumLength, Map<String, DimensionFilter> targetFilterMap, boolean isRealData, BISession session, NameObject targetSort, TargetFilter filter, List<TargetFilter> authFilter, boolean showSum, boolean setIndex, boolean calAllPage, BIMultiThreadExecutor executor, NodeCreator nodeCreator, int maxSize) {
        super(metricGroupInfoList, rowDimension, usedTargets, sumLength, targetFilterMap, isRealData, session, targetSort, filter, authFilter, showSum, setIndex, calAllPage, executor, nodeCreator);
        this.maxSize = maxSize;
    }

    protected void createAllNodeCreator(MergeIteratorCreator[] mergeIteratorCreators, int index, DimensionFilter filter, NameObject targetSort, MergeIteratorCreator creator, BIMultiThreadExecutor executor, List<TargetAndKey>[] metricsToCalculate, Map<String, TargetCalculator> calculatedMap, boolean hasAllConfigureMetricsFilter, List<CalCalculator> calCalculators) {
        //如果不过滤配置类计算指标, 就直接过滤, 过滤是配置类计算需要等到全部计算完了才过滤
        mergeIteratorCreators[index] = new LimitedAllNodeMergeIteratorCreator(hasAllConfigureMetricsFilter ? null : filter, targetSort, metricsToCalculate, calculatedMap, creator, executor, calCalculators, maxSize);
    }

    protected ConstructedRootDimensionGroup createConstructedRootDimensionGroup(boolean canPreFilter) {
        return new PartConstructedRootDimensionGroup(metricGroupInfoList, createAllNodeMergeIteratorCreator(), sumLength, session, isRealData, dimensionTargetSort, getCalCalculators(), canPreFilter || !hasDimensionInDirectFilter() ? null : rowDimension, setIndex, hasInSumMetric(), executor, calAllPage, nodeCreator, maxSize);
    }
}
