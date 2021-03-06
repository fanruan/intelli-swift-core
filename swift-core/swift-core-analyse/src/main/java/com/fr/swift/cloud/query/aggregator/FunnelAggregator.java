package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.traversal.TraversalAction;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.aggregator.funnel.MergeIterator;
import com.fr.swift.cloud.query.column.ComplexColumn;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.filter.match.MatchFilter;
import com.fr.swift.cloud.query.group.FunnelGroupKey;
import com.fr.swift.cloud.query.group.by.GroupBy;
import com.fr.swift.cloud.query.group.by.GroupByEntry;
import com.fr.swift.cloud.query.group.by.GroupByResult;
import com.fr.swift.cloud.query.info.funnel.FunnelAggregationBean;
import com.fr.swift.cloud.query.info.funnel.FunnelAssociationBean;
import com.fr.swift.cloud.query.info.funnel.FunnelVirtualStep;
import com.fr.swift.cloud.query.info.funnel.ParameterColumnsBean;
import com.fr.swift.cloud.query.info.funnel.filter.TimeFilterInfo;
import com.fr.swift.cloud.query.info.funnel.group.post.PostGroupBean;
import com.fr.swift.cloud.query.query.funnel.IHead;
import com.fr.swift.cloud.query.query.funnel.IStep;
import com.fr.swift.cloud.query.query.funnel.TimeWindowFilter;
import com.fr.swift.cloud.query.query.funnel.impl.step.VirtualStep;
import com.fr.swift.cloud.query.query.funnel.impl.window.NoRepeatTimeWindowFilter;
import com.fr.swift.cloud.query.query.funnel.impl.window.RepeatTimeWindowFilter;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.structure.array.IntList;
import com.fr.swift.cloud.structure.array.IntListFactory;
import com.fr.swift.cloud.structure.iterator.IntListRowTraversal;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2019-07-10
 */
public class FunnelAggregator extends MultiColumnAggregator<FunnelAggregatorValue> {

    private FunnelAggregationBean bean;
    private List<DetailFilter> eventFilters;
    private MatchFilter timeGroupFilter;
    private ColumnKey eventKey;

    public FunnelAggregator(FunnelAggregationBean bean) {
        this.bean = bean;
        this.eventKey = new ColumnKey(bean.getColumn());
    }

    @Override
    public FunnelAggregatorValue aggregate(RowTraversal traversal, Map<ColumnKey, Column<?>> columns) {
        ParameterColumnsBean paramColumn = bean.getParamColumns();
        // FIXME 2019/07/11 这边依赖分组排序有风险，排序可能不准确导致数据出错
        GroupByResult groupByResult = GroupBy.createGroupByResult(columns.get(new ColumnKey(paramColumn.getTimestamp())), traversal, true);
        final IntList intList = IntListFactory.createIntList();
        while (groupByResult.hasNext()) {
            groupByResult.next().getTraversal().traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    intList.add(row);
                }
            });
        }
        traversal = new IntListRowTraversal(intList);
        IStep step = createStep(bean.getSteps(), columns);
        TimeWindowFilter filter = createTimeWindowFilter(step, columns);
        SwiftLoggers.getLogger().debug("seg rows: {}", traversal.getCardinality());
        Column<String> idColumn = (Column<String>) columns.get(new ColumnKey(paramColumn.getId()));
        Iterator<GroupByEntry> iterator = GroupBy.createGroupByResult(idColumn, traversal, true);
        MergeIterator mergeIterator = new MergeIterator(filter, step, iterator,
                idColumn.getDictionaryEncodedColumn(),
                columns.get(new ColumnKey(paramColumn.getTimestamp())).getDetailColumn(),
                columns.get(eventKey).getDictionaryEncodedColumn(),
                createAssociatedColumn(columns), getPostGroupColumn(columns), getPostGroupStep());

        Map<FunnelGroupKey, FunnelHelperValue> results = new HashMap<FunnelGroupKey, FunnelHelperValue>();

        while (mergeIterator.hasNext()) {
            mergeIterator.record();
            aggregate(results, bean, filter.getResult());
            filter.reset();
        }
        SwiftLoggers.getLogger().debug("segment result: {}", results.toString());
        return new FunnelAggregatorValue(results);
    }

    @Override
    public FunnelAggregatorValue aggregate(RowTraversal traversal, Column<?> column) {
        return aggregate(traversal, ((ComplexColumn) column).getColumns());
    }

    private int getPostGroupStep() {
        PostGroupBean postGroup = bean.getPostGroup();
        return null != postGroup ? postGroup.getFunnelIndex() : -1;
    }

    private DictionaryEncodedColumn createAssociatedColumn(Map<ColumnKey, Column<?>> columns) {
        FunnelAssociationBean associationFilterBean = bean.getAssociation();
        if (associationFilterBean == null) {
            return null;
        }
        return columns.get(new ColumnKey(associationFilterBean.getColumn())).getDictionaryEncodedColumn();
    }

    private boolean[] getFlags(List<Integer> steps) {
        boolean[] flags = new boolean[bean.getSteps().size()];
        for (Integer step : steps) {
            flags[step] = true;
        }
        return flags;
    }

    private Column getPostGroupColumn(Map<ColumnKey, Column<?>> columns) {
        PostGroupBean postGroup = bean.getPostGroup();
        if (null != postGroup) {
            return columns.get(new ColumnKey(postGroup.getColumn()));
        }
        return null;
    }

    @Override
    public FunnelAggregatorValue createAggregatorValue(AggregatorValue<?> value) {
        throw new UnsupportedOperationException("funnel createAggregatorValue unsupported");
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.FUNNEL;
    }

    @Override
    public void combine(FunnelAggregatorValue current, FunnelAggregatorValue other) {
        current.combine(other);
    }

    public void setEventFilters(List<DetailFilter> eventFilters) {
        this.eventFilters = eventFilters;
    }

    public void setTimeGroupFilter(MatchFilter timeGroupFilter) {
        this.timeGroupFilter = timeGroupFilter;
    }

    private IStep createStep(List<FunnelVirtualStep> stepNames, Map<ColumnKey, Column<?>> columns) {
        Set<FunnelVirtualStep> names = new HashSet<FunnelVirtualStep>();
        names.add(stepNames.get(0));
        boolean isHeadRepeated = false;
        DictionaryEncodedColumn eventDict = columns.get(eventKey).getDictionaryEncodedColumn();
        boolean[][] steps = new boolean[stepNames.size()][eventDict.size()];
        List<ImmutableBitMap> events = new ArrayList<ImmutableBitMap>();
        for (int i = 0; i < stepNames.size(); i++) {
            FunnelVirtualStep eventBean = stepNames.get(i);
            if (i > 0 && names.contains(eventBean)) {
                isHeadRepeated = true;
            }
            List<String> eventStep = eventBean.getEvents();
            for (String s : eventStep) {
                int index = eventDict.getIndex(s);
                steps[i][index] = true;
            }
            events.add(eventFilters.get(i).createFilterIndex());
        }
        names.clear();
        names.addAll(stepNames);
        return new VirtualStep(steps, isHeadRepeated, names.size() < stepNames.size(), events);
    }

    private TimeWindowFilter createTimeWindowFilter(IStep step, Map<ColumnKey, Column<?>> columns) {
        FunnelAssociationBean association = bean.getAssociation();
        boolean[] associatedProperty = getFlags(association == null ? new ArrayList<Integer>() : association.getEvents());
        DictionaryEncodedColumn associatedPropertyColumn = null;
        if (association != null && association.getColumn() != null) {
            associatedPropertyColumn = columns.get(new ColumnKey(association.getColumn())).getDictionaryEncodedColumn();
        }
        TimeFilterInfo dayFilterBean = bean.getTimeFilter();
        int firstAssociatedIndex = (association == null || association.getEvents().size() == 0) ? -1 : association.getEvents().get(0);
        boolean repeated = step.hasRepeatedEvents();
        if (!repeated) {
            step = step.toNoRepeatedStep();
            return new NoRepeatTimeWindowFilter(bean.getTimeWindow(), bean.getTimeGroup(), timeGroupFilter, dayFilterBean,
                    step, firstAssociatedIndex, associatedProperty, associatedPropertyColumn);
        }
        return new RepeatTimeWindowFilter(bean.getTimeWindow(), bean.getTimeGroup(), timeGroupFilter, dayFilterBean,
                step, firstAssociatedIndex, associatedProperty);
    }

    private void aggregate(Map<FunnelGroupKey, FunnelHelperValue> result, FunnelAggregationBean bean, List<IHead> heads) {
        int numberOfSteps = bean.getSteps().size();
        boolean calMedian = bean.isCalculateTime();
        int postGroupStep = getPostGroupStep();
        PostGroupBean groupBean = bean.getPostGroup();
        List<double[]> rangePairs = groupBean == null ? new ArrayList<double[]>() : groupBean.getRangePairs();

        for (IHead head : heads) {
            FunnelGroupKey groupKey = createGroupKey(postGroupStep, rangePairs, head);
            FunnelHelperValue contestAggValue = result.get(groupKey);
            if (contestAggValue == null) {
                int[] counter = new int[numberOfSteps];
                List<List<Long>> lists = new ArrayList<List<Long>>();
                if (calMedian) {
                    lists = createList(numberOfSteps - 1);
                }
                contestAggValue = new FunnelHelperValue(counter, lists);
                result.put(groupKey, contestAggValue);
            }
            int[] counter = contestAggValue.getCount();
            int size = head.getSize();
            for (int i = 0; i < size; i++) {
                counter[i]++;
            }
            if (calMedian) {
                List<List<Long>> lists = contestAggValue.getPeriods();
                long[] timestamps = head.getTimestamps();
                for (int i = 1; i < size; i++) {
                    lists.get(i - 1).add((timestamps[i] - timestamps[i - 1]));
                }
            }
        }
    }

    private List<List<Long>> createList(int len) {
        List<List<Long>> lists = new ArrayList<List<Long>>();
        for (int i = 0; i < len; i++) {
            lists.add(new ArrayList<Long>());
        }
        return lists;
    }

    private FunnelGroupKey createGroupKey(int postGroupStep, List<double[]> rangePairs, IHead head) {
        FunnelGroupKey groupKey;
        Object groupValue = head.getGroupValue();
        if (postGroupStep != -1) {
            if (null == rangePairs || rangePairs.isEmpty()) {
                if (groupValue != null) {
                    groupKey = new FunnelGroupKey(head.getDate(), 0, (String) groupValue);
                } else {
                    groupKey = new FunnelGroupKey(head.getDate(), 0, "");
                }
            } else {
                Double price = (Double) groupValue;
                int priceGroup = -1;
                if (price != null) {
                    for (int j = 0; j < rangePairs.size(); j++) {
                        if (price >= rangePairs.get(j)[0] && price < rangePairs.get(j)[1]) {
                            priceGroup = j;
                            break;
                        }
                    }
                }
                List<Double> pair = priceGroup == -1 ? new ArrayList<Double>() :
                        Arrays.asList(rangePairs.get(priceGroup)[0], rangePairs.get(priceGroup)[1]);
                groupKey = new FunnelGroupKey(head.getDate(), priceGroup, pair);
            }
        } else {
            groupKey = new FunnelGroupKey(head.getDate());
        }
        return groupKey;
    }
}
