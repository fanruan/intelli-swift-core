package com.fr.bi.cal.analyze.cal.sssecret;


import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.cal.Executor.Executor;
import com.fr.bi.cal.analyze.cal.Executor.ExecutorPartner;
import com.fr.bi.cal.analyze.cal.Executor.ILazyExecutorOperation;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.cal.sssecret.mergeiter.MergeIterator;
import com.fr.bi.cal.analyze.exception.TerminateExecutorException;
import com.fr.bi.conf.data.source.TableSourceUtils;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.cal.DimensionIterator;
import com.fr.bi.stable.engine.cal.DimensionIteratorCreator;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.sortlist.ArrayLookupHelper;
import com.fr.bi.stable.operation.group.BIGroupUtils;
import com.fr.bi.report.result.DimensionCalculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * TODO 需要改成可以半路计算，提升即时计算性能
 * 即可以用之前已经计算好的结果继续计算
 *
 * @author Daniel
 *         分页机制，使用另外一个线程来判断计算当前已经计算了多少结果了 并取数
 */
public class SingleDimensionGroup extends ExecutorPartner implements ILazyExecutorOperation<MetricMergeResult, MetricMergeResult>, ISingleDimensionGroup {


    private static int demoGroupLimit = BIBaseConstant.PART_DATA_GROUP_LIMIT;

    protected ICubeTableService[] tis;
    protected MergeIteratorCreator mergeIteratorCreator;
    protected ICubeDataLoader loader;
    private BusinessTable[] metricTables;
    private List<TargetAndKey>[] summaryLists;
    private DimensionCalculator[] columns;
    private ICubeValueEntryGetter[] getters;
    private GroupValueIndex[] gvis;
    private Object[] data;
    private boolean useRealData = true;
    private List<MetricMergeResult> metricMergeResultList = new ArrayList<MetricMergeResult>();
    private int sumLength;


    /**
     * Group计算的构造函数
     */
    protected SingleDimensionGroup(BusinessTable[] metricTables, List<TargetAndKey>[] summaryLists, int sumLength, ICubeTableService[] tis, DimensionCalculator[] columns, ICubeValueEntryGetter[] getters, Object[] data, GroupValueIndex[] gvis, MergeIteratorCreator mergeIteratorCreator, ICubeDataLoader loader, boolean useRealData) {
        this.metricTables = metricTables;
        this.summaryLists = summaryLists;
        this.sumLength = sumLength;
        this.tis = tis;
        this.columns = columns;
        this.getters = getters;
        this.data = data;
        this.loader = loader;
        this.gvis = gvis;
        this.mergeIteratorCreator = mergeIteratorCreator;
        this.useRealData = useRealData;
        turnOnExecutor();
    }

    public static SingleDimensionGroup createDimensionGroup(BusinessTable[] metricTables, List<TargetAndKey>[] summaryLists, int sumLength, ICubeTableService[] tis, DimensionCalculator[] columns, ICubeValueEntryGetter[] getters, final Object[] data, GroupValueIndex[] gvis, MergeIteratorCreator mergeIteratorCreator, ICubeDataLoader loader, boolean useRealData) {
        return new SingleDimensionGroup(metricTables, summaryLists, sumLength, tis, columns, getters, data, gvis, mergeIteratorCreator, loader, useRealData);
    }


    protected Iterator getIterator() {
        DimensionIterator[] iterators = new DimensionIterator[metricTables.length];
        for (int i = 0; i < iterators.length; i++) {
            DimensionIterator it = getIterator(i);
            if (!columns[i].getDirectToDimensionRelationList().isEmpty()) {
                it = new DirectToDimensionRelationIterator(it, columns[i], loader);
            }
            iterators[i] = it;
        }
        return mergeIteratorCreator.createIterator(iterators, sumLength, gvis, columns[0].getComparator(), tis, loader);
    }

    public void turnOnExecutor() {
        this.lazyExecutor = new Executor();
        Iterator iterator = getIterator();
        this.lazyExecutor.initial(this, iterator);
    }

    protected DimensionIterator getIterator(int index) {
        if (gvis[index] == null || GVIUtils.isAllEmptyRoaringGroupValueIndex(gvis[index])) {
            return MergeIterator.EMPTY;
        }
        boolean urd = useRealData;
        int groupLimit = demoGroupLimit;
        if (!useRealData) {
            long rowCount = tis[index] == null ? loader.getTableIndex(columns[index].getField().getTableBelongTo().getTableSource()).getRowCount() : tis[index].getRowCount();
            if (rowCount < BIBaseConstant.PART_DATA_COUNT_LIMIT) {
                urd = true;
            } else {
                long groupCount = loader.getTableIndex(columns[index].getField().getTableBelongTo().getTableSource()).loadGroup(columns[index].createKey(), new ArrayList<BITableSourceRelation>()).nonPrecisionSize();
                groupLimit = (int) (groupCount * BIBaseConstant.PART_DATA_COUNT_LIMIT / rowCount);
                groupLimit = Math.min(Math.max(BIBaseConstant.PART_DATA_GROUP_LIMIT, groupLimit), BIBaseConstant.PART_DATA_GROUP_MAX_LIMIT);
            }
        }
        //自循环处理同自定义分组
        if (!urd || hasSpecialGroup(columns[index]) || isCirCle(columns[index])) {
            return dealWithSpecialGroupOrNotRealData(index, urd, groupLimit);
        }
        return getIterByAllCal(index);
    }

    private DimensionIterator dealWithSpecialGroupOrNotRealData(int index, boolean urd, int groupLimit) {
        final Iterator it = columns[index].createValueMapIterator(metricTables[index], loader, urd, groupLimit, gvis[index]);
        return new DimensionIterator() {
            @Override
            public void remove() {
            }

            @Override
            public boolean isReturnFinalGroupValueIndex() {
                return false;
            }

            @Override
            public int getCurrentGroup() {
                return 0;
            }

            @Override
            public boolean canReGainGroupValueIndex() {
                return false;
            }

            @Override
            public GroupValueIndex getGroupValueIndexByGroupIndex(int groupIndex) {
                return null;
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Map.Entry<Object, GroupValueIndex> next() {
                return (Map.Entry<Object, GroupValueIndex>) it.next();
            }
        };
    }

    private boolean isCirCle(DimensionCalculator column) {
        List<BITableSourceRelation> relations = column.getRelationList();
        for (BITableSourceRelation relation : relations) {
            if (TableSourceUtils.isSelfCirCleSource(relation.getForeignTable()) && TableSourceUtils.isSelfCircleParentField(relation.getForeignField())) {
                return true;
            }
        }
        return false;
    }


    private boolean hasSpecialGroup(DimensionCalculator column) {
        if (BIGroupUtils.isCustomGroup(column.getGroup())) {
            return true;
        }
        if (column.getSortType() == BIReportConstant.SORT.CUSTOM) {
            return true;
        }
        return false;
    }


    private DimensionIterator getIterByAllCal(int index) {
        boolean asc = !(columns[index].getSortType() == BIReportConstant.SORT.DESC || columns[index].getSortType() == BIReportConstant.SORT.NUMBER_DESC);
        return DimensionIteratorCreator.createValueMapIterator(getters[index], gvis[index], asc);
    }

    @Override
    public int getChildIndexByValue(Object value) {
        return ArrayLookupHelper.lookup(new MetricMergeResult[]{new MetricMergeResult(value, sumLength, null)}, new ArrayLookupHelper.Lookup<MetricMergeResult>() {
            @Override
            public int minIndex() {
                return 0;
            }

            @Override
            public int maxIndex() {
                return getChildLength() - 1;
            }

            @Override
            public MetricMergeResult lookupByIndex(int index) {
                return getMetricMergeResultByWait(index);
            }

            @Override
            public int compare(MetricMergeResult t1, MetricMergeResult t2) {
                return columns[0].getComparator().compare(t1.getData(), t2.getData());
            }
        })[0];
    }

    @Override
    public NoneDimensionGroup getChildDimensionGroup(int row) {
        MetricMergeResult metricMergeResult = getMetricMergeResultByWait(row);
        if (isNull(metricMergeResult)) {
            return NoneDimensionGroup.NULL;
        }
        return NoneDimensionGroup.createDimensionGroup(metricTables, summaryLists, sumLength, tis, metricMergeResult, loader);
    }

    private boolean isNull(MetricMergeResult node) {
        return node == MetricMergeResult.NULL;
    }

    @Override
    public Object getChildData(int row) {
        MetricMergeResult metricMergeResult = getMetricMergeResultByWait(row);
        return metricMergeResult.getData();
    }

    @Override
    public String getChildShowName(int row) {
        MetricMergeResult metricMergeResult = getMetricMergeResultByWait(row);
        return metricMergeResult.getShowValue();
    }

    protected int getChildLength() {
        return metricMergeResultList.size();
    }

    protected MetricMergeResult getMetricMergeResultByWait(int row) {
        waitExecutor(row);
        if (row < metricMergeResultList.size()) {
            return metricMergeResultList.get(row);
        } else {
            return MetricMergeResult.NULL;
        }
    }

    @Override
    public MetricMergeResult mainTaskConditions(MetricMergeResult result) {
        return result;
    }

    @Override
    public boolean jumpCurrentOne(MetricMergeResult para) throws TerminateExecutorException {
        for (GroupValueIndex gvi : para.getGvis()) {
            if (!indexIsAllEmpty(gvi)) {
                return false;
            }
        }
        return true;
    }

    private boolean indexIsAllEmpty(GroupValueIndex gvi) {
        return gvi == null || gvi.isAllEmpty();
    }

    @Override
    public void mainTask(MetricMergeResult obj, MetricMergeResult metricMergeResult) throws TerminateExecutorException {
        addMetricMergeResult(metricMergeResult);
    }

    private void addMetricMergeResult(MetricMergeResult metricMergeResult) {
        metricMergeResultList.add(metricMergeResult);
    }

    /**
     * 释放资源，之前需要释放的，现在暂时没有什么需要释放的
     */
    @Override
    public void release() {
    }

    @Override
    public Object[] getData() {
        return data;
    }

}