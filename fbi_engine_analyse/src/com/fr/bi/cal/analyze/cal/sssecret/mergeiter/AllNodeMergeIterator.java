package com.fr.bi.cal.analyze.cal.sssecret.mergeiter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCall;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.general.NameObject;

import java.util.*;

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

    public AllNodeMergeIterator(Iterator<MetricMergeResult> mergeIterator, DimensionFilter filter, NameObject targetSort, List<TargetAndKey>[] metricsToCalculate, Map<String, TargetCalculator> calculatedMap, ICubeTableService[] tis, ICubeDataLoader loader) {
        this.mergeIterator = mergeIterator;
        this.filter = filter;
        this.metricsToCalculate = metricsToCalculate;
        this.calculatedMap = calculatedMap;
        this.tis = tis;
        this.loader = loader;
        this.targetSort = targetSort;
        initIter();
    }

    private void initIter() {
        root = new Node(null);
        while (mergeIterator.hasNext()) {
            MetricMergeResult result = mergeIterator.next();
            checkSum(result);
            root.addChild(result);
        }
        List<MetricMergeResult> resultList = new ArrayList<MetricMergeResult>();
        for (Node node : root.getChilds()) {
            if (filter == null || filter.showNode(node, calculatedMap, loader)) {
                resultList.add((MetricMergeResult) node);
            }
        }
        checkSort(resultList);
        resultIter = resultList.iterator();
    }

    private void checkSum(MetricMergeResult result) {
        GroupValueIndex[] gvis = result.getGvis();
        if (metricsToCalculate != null) {
            for (int i = 0; i < metricsToCalculate.length; i++) {
                List<TargetAndKey> targetAndKeys = metricsToCalculate[i];
                if (targetAndKeys != null) {
                    for (TargetAndKey targetAndKey : targetAndKeys) {
                        new SummaryCall(tis[i], result, targetAndKey, gvis[i], loader).cal();
                    }
                }
            }
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
