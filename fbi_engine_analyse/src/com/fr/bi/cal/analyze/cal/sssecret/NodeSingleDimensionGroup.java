package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;

import java.util.List;

/**
 * Created by 小灰灰 on 2017/1/18.
 */
public class NodeSingleDimensionGroup extends SingleDimensionGroup {

    private List<Node> children;

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
     */
    protected NodeSingleDimensionGroup(BusinessTable[] metricTables, List<TargetAndKey>[] summaryLists, int sumLength, ICubeTableService[] tis, DimensionCalculator[] columns, ICubeValueEntryGetter[] getters, Object[] data, GroupValueIndex[] gvis, MergeIteratorCreator mergeIteratorCreator, ICubeDataLoader loader, List<Node> children) {
        super(metricTables, summaryLists, sumLength, tis, columns, getters, data, gvis, mergeIteratorCreator, loader, true);
        this.children = children;
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


    public static ISingleDimensionGroup createDimensionGroup(BusinessTable[] metrics, List<TargetAndKey>[] summaryLists, int sumLength, ICubeTableService[] tis, DimensionCalculator[] columns, ICubeValueEntryGetter[] getters, Object[] data, GroupValueIndex[] gvis, MergeIteratorCreator mergeIteratorCreator, ICubeDataLoader loader, List<Node> metricMergeResultList) {
        return new NodeSingleDimensionGroup(metrics, summaryLists, sumLength, tis, columns, getters, data, gvis, mergeIteratorCreator, loader, metricMergeResultList);
    }
}
