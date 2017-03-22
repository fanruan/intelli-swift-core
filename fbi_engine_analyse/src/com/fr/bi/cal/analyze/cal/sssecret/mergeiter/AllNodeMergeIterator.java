package com.fr.bi.cal.analyze.cal.sssecret.mergeiter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCall;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.general.NameObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 小灰灰 on 2017/1/9.
 */
public class AllNodeMergeIterator implements Iterator<MetricMergeResult> {
    private DimensionFilter filter;
    private NameObject targetSort;
    private List<TargetAndKey>[] metricsToCalculate;
    private Map<String, TargetCalculator> calculatedMap;
    private ICubeTableService[] tis;
    private ICubeDataLoader loader;
    private Node root;
    private Iterator<MetricMergeResult> mergeIterator;
    private Iterator<MetricMergeResult> resultIter;
    //线程池是否已经计算完成
    private volatile boolean completed;
    //是否已经全部加到线程池
    private volatile boolean allAdded;
    //已经完成计算的数量
    private AtomicInteger count;
    //丢近线程池的计算的数量
    private int size;

    private BIMultiThreadExecutor executor;

    private List<CalCalculator> formulaCalculator;

    public AllNodeMergeIterator(Iterator<MetricMergeResult> mergeIterator, DimensionFilter filter, NameObject targetSort, List<TargetAndKey>[] metricsToCalculate, Map<String, TargetCalculator> calculatedMap, ICubeTableService[] tis, ICubeDataLoader loader, BIMultiThreadExecutor executor, List<CalCalculator> formulaCalculator) {
        this.mergeIterator = mergeIterator;
        this.filter = filter;
        this.metricsToCalculate = metricsToCalculate;
        this.calculatedMap = calculatedMap;
        this.tis = tis;
        this.loader = loader;
        this.targetSort = targetSort;
        this.executor = executor;
        this.formulaCalculator = formulaCalculator;
        initIter();
    }

    private void initIter() {
        root = new Node(null);
        count = new AtomicInteger(0);
        //不是多线程，或者没有指标都表示线程池的计算已经结束
        completed = getMetricsSize() == 0 || executor == null;
        allAdded = false;
        while (mergeIterator.hasNext()) {
            MetricMergeResult result = mergeIterator.next();
            checkSum(result);
            root.addChild(result);
            ++size;
        }
        if (metricsToCalculate != null) {
            size *= getMetricsSize();
        }
        allAdded = true;
        //如果多线程计算没有结束，就等结束
        //有可能在设置allAdded = true之前就结束了，导致 checkComplete 没执行，这边还要判断下size
        completed = completed || count.get() == size;
        if (!completed) {
            //没完成的话要唤醒下executor，以防有加到executor里wait住的。
            executor.wakeUp();
            synchronized (this) {
                if (!completed) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        checkFormulaMetrics();
        List<MetricMergeResult> resultList = new ArrayList<MetricMergeResult>();
        //需要全部构建好才能处理的过滤，比如前2个或者后5个这种，不能在汇总值算完就过滤
        for (Node node : root.getChilds()) {
            if (filter == null || filter.showNode(node, calculatedMap, loader)) {
                resultList.add((MetricMergeResult) node);
            }
        }
        checkSort(resultList);
        resultIter = resultList.iterator();
    }

    private void checkFormulaMetrics() {
        if (formulaCalculator != null && !formulaCalculator.isEmpty()) {
            List<TargetCalculator> calculatorList = new ArrayList<TargetCalculator>();
            for (List<TargetAndKey> keys : metricsToCalculate) {
                if (keys != null) {
                    for (TargetAndKey key : keys) {
                        if (key != null) {
                            calculatorList.add(key.getCalculator());
                        }
                    }
                }

            }
            List<CalCalculator> formulaCalculator = new ArrayList<CalCalculator>();
            formulaCalculator.addAll(this.formulaCalculator);
            CubeIndexLoader.calculateTargets(calculatorList, formulaCalculator, root);
        }
    }

    private void checkComplete() {
        //如果已经计算完了加入线程池的计算，并且所有计算都已经加入线程池了，就说明计算完了，唤醒下等待的线程。
        if (count.incrementAndGet() == size && allAdded) {
            synchronized (this) {
                completed = true;
                this.notify();
            }
        }
    }

    private int getMetricsSize() {
        int size = 0;
        if (metricsToCalculate != null) {
            for (int i = 0; i < metricsToCalculate.length; i++) {
                size += metricsToCalculate[i].size();
            }
        }
        return size;
    }

    private void checkSum(MetricMergeResult result) {
        GroupValueIndex[] gvis = result.getGvis();
        if (metricsToCalculate != null) {
            for (int i = 0; i < metricsToCalculate.length; i++) {
                List<TargetAndKey> targetAndKeys = metricsToCalculate[i];
                if (targetAndKeys != null) {
                    for (TargetAndKey targetAndKey : targetAndKeys) {
                        if (executor != null) {
                            executor.add(new SummaryCountCal(tis[i], result, targetAndKey, gvis[i], loader));
                        } else {
                            new SummaryCountCal(tis[i], result, targetAndKey, gvis[i], loader).cal();
                        }
                    }
                }
            }
        }
    }

    private class SummaryCountCal extends SummaryCall {

        public SummaryCountCal(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader) {
            super(ti, node, targetAndKey, gvi, loader);
        }

        @Override
        public void cal() {
            try {
                super.cal();
            } finally {
                checkComplete();
            }
            //      ((MetricMergeResult) node).clearGvis();
        }
    }

    private void checkSort(List<MetricMergeResult> resultList) {
        if (targetSort != null) {
            String sortTarget = targetSort.getName();
            final TargetGettingKey key = calculatedMap.get(sortTarget).createTargetGettingKey();
            final int sortType = (Integer) (targetSort.getObject());
            Collections.sort(resultList, new Comparator<MetricMergeResult>() {
                @Override
                public int compare(MetricMergeResult o1, MetricMergeResult o2) {
                    Number v1 = o1.getSummaryValue(key);
                    Number v2 = o2.getSummaryValue(key);
                    if (v1 == v2) {
                        return 0;
                    }
                    if (v1 == null) {
                        return 1;
                    }
                    if (v2 == null) {
                        return -1;
                    }
                    if (v1.doubleValue() == v2.doubleValue()) {
                        return 0;
                    }
                    boolean v = v1.doubleValue() < v2.doubleValue();
                    return (sortType == BIReportConstant.SORT.ASC) == v ? -1 : 1;
                }
            });
        }
    }

    @Override
    public boolean hasNext() {
        return resultIter.hasNext();
    }

    @Override
    public MetricMergeResult next() {
        return resultIter.next();
    }

    @Override
    public void remove() {

    }
}
