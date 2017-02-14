package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCall;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.AllNodeMergeIteratorCreator;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 小灰灰 on 2017/1/18.
 */
public class NodeSingleDimensionGroup extends SingleDimensionGroup {

    private List<Node> children;
    private TargetAndKey sortTarget;
    private int sortIndex;
    private int sortType;
    private DimensionFilter filter;

    /**
     * Group计算的构造函数
     *  @param metricTables
     * @param summaryLists
     * @param tis
     * @param columns
     * @param getters
     * @param data
     * @param gvis
     * @param mergeIteratorCreator
     * @param loader
     * @param filter
     */
    protected NodeSingleDimensionGroup(BusinessTable[] metricTables, List<TargetAndKey>[] summaryLists, ICubeTableService[] tis, DimensionCalculator[] columns, ICubeValueEntryGetter[] getters, Object[] data, GroupValueIndex[] gvis, MergeIteratorCreator mergeIteratorCreator, ICubeDataLoader loader, List<Node> children, TargetAndKey sortTarget, int sortIndex, int sortType, DimensionFilter filter) {
        super(metricTables, summaryLists, tis, columns, getters, data, gvis, mergeIteratorCreator, loader, true);
        this.children = children;
        this.sortTarget = sortTarget;
        this.sortIndex = sortIndex;
        this.sortType = sortType;
        this.filter = filter;
        checkFilter();
        checkSort();
    }

    private void checkFilter() {
        if (filter != null){
            List<Node> tempC = new ArrayList<Node>();
            for (Node n : children){
                if (filter.showNode(n, ((AllNodeMergeIteratorCreator)mergeIteratorCreator).getCalculatedMap(), loader)){
                    tempC.add(n);
                }
            }
            children = tempC;
        }
    }

    private void checkSort() {
        if (sortTarget != null && children.size() > 1){
            for (int i = 0; i < getChildLength(); i++){
                MetricMergeResult child = getMetricMergeResultByWait(i);
                new SummaryCall(tis[sortIndex], child, sortTarget, child.getGvis()[sortIndex], loader).cal();
            }
            Collections.sort(children, new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    Number v1 = o1.getSummaryValue(sortTarget.getTargetGettingKey());
                    Number v2 = o2.getSummaryValue(sortTarget.getTargetGettingKey());
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

    public void  turnOnExecutor(){

    }

    protected int getChildLength(){
        return children.size();
    }

    protected MetricMergeResult getMetricMergeResultByWait(int row) {
        if (row < children.size()) {
            return (MetricMergeResult) children.get(row);
        }
        return MetricMergeResult.NULL;
    }

    @Override
    public NoneDimensionGroup getChildDimensionGroup(int row) {
        NoneDimensionGroup noneDimensionGroup = super.getChildDimensionGroup(row);
        MetricMergeResult metricMergeResult = getMetricMergeResultByWait(row);
        noneDimensionGroup.setChildren(metricMergeResult.getChilds());
        return noneDimensionGroup;
    }


    public static ISingleDimensionGroup createDimensionGroup(BusinessTable[] metrics, List<TargetAndKey>[] summaryLists, ICubeTableService[] tis, DimensionCalculator[] columns, ICubeValueEntryGetter[] getters, Object[] data, GroupValueIndex[] gvis, MergeIteratorCreator mergeIteratorCreator, ICubeDataLoader loader, List<Node> metricMergeResultList, TargetAndKey sortTarget, int sortIndex, int sortType, DimensionFilter filter) {
        return new NodeSingleDimensionGroup(metrics, summaryLists, tis, columns, getters, data, gvis, mergeIteratorCreator, loader, metricMergeResultList, sortTarget, sortIndex, sortType, filter);
    }
}
