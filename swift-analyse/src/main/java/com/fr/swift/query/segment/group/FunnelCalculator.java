package com.fr.swift.query.segment.group;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.funnel.IHead;
import com.fr.swift.query.funnel.IStep;
import com.fr.swift.query.funnel.TimeWindowFilter;
import com.fr.swift.query.funnel.impl.step.VirtualStep;
import com.fr.swift.query.funnel.impl.window.NoRepeatTimeWindowFilter;
import com.fr.swift.query.funnel.impl.window.RepeatTimeWindowFilter;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelAggregationBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelAssociationBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelEventBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.filter.TimeFilterInfo;
import com.fr.swift.query.info.bean.element.aggregation.funnel.group.post.PostGroupBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.parser.FilterInfoParser;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.funnel.FunnelQueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.iterator.RowTraversal;

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
 * @date 2019-06-28
 */
public class FunnelCalculator {

    private FunnelAggregationBean bean;
    private Segment segment;
    private Map<String, Column> dictMap;

    public FunnelCalculator(Segment segment, FunnelAggregationBean bean) {
        this.bean = bean;
        this.segment = segment;
        this.dictMap = new HashMap<String, Column>();
        Column event = segment.getColumn(new ColumnKey(bean.getColumn()));
        this.dictMap.put("event", event);
        this.dictMap.put("id", segment.getColumn(new ColumnKey(bean.getColumns().getUserId())));
        this.dictMap.put("timestamp", segment.getColumn(new ColumnKey(bean.getColumns().getTimestamp())));
        PostGroupBean postGroup = bean.getPostGroup();
        if (null != postGroup) {
            this.dictMap.put("postGroup", segment.getColumn(new ColumnKey(postGroup.getColumn())));
        }
    }

    public FunnelQueryResultSet getQueryResult() throws SwiftMetaDataException {
        IStep step = createStep(bean.getEvents(), segment);
        TimeWindowFilter filter = createTimeWindowFilter(step, bean, segment);
        RowTraversal rowTraversal =
                FilterBuilder.buildDetailFilter(segment, FilterInfoParser.parse(new SourceKey(segment.getMetaData().getTableName()),
                        bean.getFilter())).createFilterIndex();
        SwiftLoggers.getLogger().debug("seg rows: {}", rowTraversal.getCardinality());
        Iterator<GroupByEntry> iterator = GroupBy.createGroupByResult(dictMap.get("id"), rowTraversal, true);
        MergeIterator mergeIterator = new MergeIterator(filter, step, iterator,
                dictMap.get("id").getDictionaryEncodedColumn(),
                dictMap.get("timestamp").getDetailColumn(),
                dictMap.get("event").getDictionaryEncodedColumn(),
                createAssociatedColumn(), dictMap.get("postGroup"), getPostGroupStep());

        Map<FunnelGroupKey, FunnelAggValue> results = new HashMap<FunnelGroupKey, FunnelAggValue>();

        while (mergeIterator.hasNext()) {
            mergeIterator.record();
            aggregate(results, bean, filter.getResult());
            filter.reset();
        }
        SwiftLoggers.getLogger().debug("segment result: {}", results.toString());
        return new FunnelQueryResultSet(new FunnelResultSet(results));
    }

    private int getPostGroupStep() {
        PostGroupBean postGroup = bean.getPostGroup();
        return null != postGroup ? postGroup.getFunnelIndex() : -1;
    }

    private DictionaryEncodedColumn createAssociatedColumn() {
//        List<FunnelAssociationBean> associationFilterBean = bean.getAssociations();
//        if (associationFilterBean == null) {
//            return null;
//        }
//        DictionaryEncodedColumn a0 = segment.getColumn(new ColumnKey(associationFilterBean.getColumn())).getDictionaryEncodedColumn();
//        return new DictionaryEncodedColumn[]{null};
        return null;
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

    private void aggregate(Map<FunnelGroupKey, FunnelAggValue> result, FunnelAggregationBean bean, List<IHead> heads) {
        int numberOfSteps = bean.getEvents().size();
        boolean calMedian = bean.isCalculateTime();
        int postGroupStep = getPostGroupStep();
        PostGroupBean groupBean = bean.getPostGroup();
        List<double[]> rangePairs = groupBean == null ? new ArrayList<double[]>() : groupBean.getRangePairs();

        for (IHead head : heads) {
            FunnelGroupKey groupKey = createGroupKey(postGroupStep, rangePairs, head);
            FunnelAggValue contestAggValue = result.get(groupKey);
            if (contestAggValue == null) {
                int[] counter = new int[numberOfSteps];
                List<List<Long>> lists = new ArrayList<List<Long>>();
                if (calMedian) {
                    lists = createList(numberOfSteps - 1);
                }
                contestAggValue = new FunnelAggValue(counter, lists);
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


    private TimeWindowFilter createTimeWindowFilter(IStep step, FunnelAggregationBean bean, Segment segment) {
        List<FunnelAssociationBean> association = bean.getAssociations();
        boolean[] associatedProperty = new boolean[bean.getEvents().size()];//getFlags(bean, association == null ? new ArrayList<Integer>() : association.getEvents());
        DictionaryEncodedColumn associatedPropertyColumn = null;
//        if (association != null && association.getColumn() != null) {
//            associatedPropertyColumn = segment.getColumn(new ColumnKey(association.getColumn())).getDictionaryEncodedColumn();
//        }
        TimeFilterInfo dayFilterBean = bean.getTimeFilter();
        int firstAssociatedIndex = -1;//(association == null || association.getEvents().size() == 0) ? -1 : association.getEvents().get(0);
        boolean repeated = step.hasRepeatedEvents();
        FilterInfo filterInfo = FilterInfoParser.parse(new SourceKey(segment.getMetaData().getId()), bean.getTimeGroup().filter());
        MatchFilter matchFilter = FilterBuilder.buildMatchFilter(filterInfo);
        if (!repeated) {
            step = step.toNoRepeatedStep();
            return new NoRepeatTimeWindowFilter(bean.getTimeWindow(), bean.getTimeGroup(), matchFilter, dayFilterBean,
                    step, firstAssociatedIndex, associatedProperty, associatedPropertyColumn);
        }
        return new RepeatTimeWindowFilter(bean.getTimeWindow(), bean.getTimeGroup(), matchFilter, dayFilterBean,
                step, firstAssociatedIndex, associatedProperty, associatedPropertyColumn);
    }

    private boolean[] getFlags(FunnelQueryBean bean, List<Integer> steps) {
        boolean[] flags = new boolean[bean.getAggregation().getFunnelEvents().size()];
        for (Integer step : steps) {
            flags[step] = true;
        }
        return flags;
    }

    private IStep createStep(List<FunnelEventBean> stepNames, Segment segment) throws SwiftMetaDataException {
        Set<FunnelEventBean> names = new HashSet<FunnelEventBean>();
        names.add(stepNames.get(0));
        boolean isHeadRepeated = false;
        DictionaryEncodedColumn eventDict = dictMap.get("event").getDictionaryEncodedColumn();
        boolean[][] steps = new boolean[stepNames.size()][eventDict.size()];
        List<ImmutableBitMap> events = new ArrayList<ImmutableBitMap>();
        for (int i = 0; i < stepNames.size(); i++) {
            FunnelEventBean eventBean = stepNames.get(i);
            if (i > 0 && names.contains(eventBean)) {
                isHeadRepeated = true;
            }
            List<String> eventStep = eventBean.getSteps();
            for (String s : eventStep) {
                int index = eventDict.getIndex(s);
                steps[i][index] = true;
            }
            FilterInfoBean filter = eventBean.getFilter();
            if (null != filter) {
                DetailFilter detailFilter = FilterBuilder.buildDetailFilter(segment, FilterInfoParser.parse(new SourceKey(segment.getMetaData().getTableName()), filter));
                events.add(detailFilter.createFilterIndex());
            } else {
                events.add(AllShowBitMap.of(segment.getRowCount()));
            }
        }
        names.clear();
        names.addAll(stepNames);
        return new VirtualStep(steps, isHeadRepeated, names.size() < stepNames.size(), events);
    }

}
