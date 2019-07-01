package com.fr.swift.query.segment.group;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.funnel.IHead;
import com.fr.swift.query.funnel.IStep;
import com.fr.swift.query.funnel.ITimeWindowFilter;
import com.fr.swift.query.funnel.impl.GroupTWFilter;
import com.fr.swift.query.funnel.impl.TimeWindowFilter;
import com.fr.swift.query.funnel.impl.step.VirtualStep;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by.GroupByResult;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelAggregationBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelAssociationBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelEventBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.ParameterColumnsBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.filter.TimeFilterInfo;
import com.fr.swift.query.info.bean.element.aggregation.funnel.group.post.PostGroupBean;
import com.fr.swift.query.info.bean.parser.FilterInfoParser;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.funnel.FunnelQueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
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
    private DictionaryEncodedColumn eventDict;

    public FunnelCalculator(Segment segment, FunnelAggregationBean bean) {
        this.bean = bean;
        this.segment = segment;
        Column event = segment.getColumn(new ColumnKey(bean.getColumn()));
        this.eventDict = event.getDictionaryEncodedColumn();
    }

    public FunnelQueryResultSet getQueryResult() throws SwiftMetaDataException {
        IStep step = createStep(bean.getEvents());
        ITimeWindowFilter filter = createTimeWindowFilter(step, bean, segment);
        ParameterColumnsBean params = bean.getColumns();
        Column event = segment.getColumn(new ColumnKey(bean.getColumn()));
        RowTraversal rowTraversal =
                FilterBuilder.buildDetailFilter(segment, FilterInfoParser.parse(new SourceKey(segment.getMetaData().getTableName()),
                        bean.getFilter())).createFilterIndex();
        SwiftLoggers.getLogger().debug("seg rows: {}", rowTraversal.getCardinality());
        DetailColumn combineColumn = segment.getColumn(new ColumnKey(params.getTimestamp())).getDetailColumn();
        DictionaryEncodedColumn date = segment.getColumn(new ColumnKey(params.getDate())).getDictionaryEncodedColumn();
        Column idColumn = segment.getColumn(new ColumnKey(params.getUserId()));
        Iterator<GroupByEntry> iterator = GroupBy.createGroupByResult(idColumn, rowTraversal, true);
        MergeIterator mergeIterator;
        Iterator<GroupByEntry> empty = new GroupByResult() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public GroupByEntry next() {
                return null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        mergeIterator = new MergeIterator(filter, step, new Iterator[]{iterator, empty},
                new DictionaryEncodedColumn[]{idColumn.getDictionaryEncodedColumn(),
                        null},
                new DetailColumn[]{combineColumn, null},
                new DictionaryEncodedColumn[]{event.getDictionaryEncodedColumn(), null},
                new DictionaryEncodedColumn[]{date,},
                createAssociatedColumn(), createPostGroupColumn(), getPostGroupStep());

        Map<FunnelGroupKey, FunnelAggValue> results = new HashMap<FunnelGroupKey, FunnelAggValue>();

        while (mergeIterator.hasNext()) {
            mergeIterator.record();
            aggregate(results, bean, filter.getResult());
            filter.reset();
        }
        SwiftLoggers.getLogger().debug("segment result: {}", results.toString());
        return new FunnelQueryResultSet(new FunnelResultSet(results));
    }

    private Column[] createPostGroupColumn() {
        PostGroupBean postGroup = bean.getPostGroup();
        if (null == postGroup) {
            return null;
        }

        return new Column[]{segment.getColumn(new ColumnKey(postGroup.getColumn())), null};
    }

    private int getPostGroupStep() {
        PostGroupBean postGroup = bean.getPostGroup();
        return null != postGroup ? postGroup.getFunnelIndex() : -1;
    }

    private DictionaryEncodedColumn[] createAssociatedColumn() {
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
            if (rangePairs.size() == 0) {
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

//    private boolean isCalMedian() {
//        List<PostQueryInfoBean> beans = bean.getPostAggregations();
//        for (PostQueryInfoBean postQueryInfoBean : beans) {
//            if (postQueryInfoBean.getType() == PostQueryType.FUNNEL_MEDIAN) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private int getPostGroupStep() {
//        PostGroupBean groupBean = bean.getAggregation().getPostGroup();
//        return groupBean == null ? -1 : groupBean.getFunnelIndex();
//    }

    private void aggregate(Map<FunnelGroupKey, FunnelAggValue> result, FunnelAggregationBean bean, List<IHead> heads) {
        int numberOfSteps = bean.getEvents().size();
        boolean calMedian = false;//isCalMedian();
        int postGroupStep = getPostGroupStep();
        PostGroupBean groupBean = bean.getPostGroup();
        List<double[]> rangePairs = groupBean == null ? new ArrayList<double[]>() : groupBean.getRangePairs();

        for (IHead head : heads) {
            FunnelGroupKey groupKey = createGroupKey(postGroupStep, rangePairs, head);
            FunnelAggValue contestAggValue = result.get(groupKey);
            if (contestAggValue == null) {
                int[] counter = new int[numberOfSteps];
                List<List<Integer>> lists = new ArrayList<List<Integer>>();
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
//            if (calMedian) {
//                List<List<Integer>> lists = contestAggValue.getPeriods();
//                long[] timestamps = head.getTimestamps();
//                for (int i = 1; i < size; i++) {
//                    lists.get(i - 1).add((timestamps[i] - timestamps[i - 1]));
//                }
//            }
        }
    }

    private List<List<Integer>> createList(int len) {
        List<List<Integer>> lists = new ArrayList<List<Integer>>();
        for (int i = 0; i < len; i++) {
            lists.add(new ArrayList<Integer>());
        }
        return lists;
    }


    private ITimeWindowFilter createTimeWindowFilter(IStep step, FunnelAggregationBean bean, Segment segment) {
        List<FunnelAssociationBean> association = bean.getAssociations();
        boolean[] associatedProperty = new boolean[bean.getEvents().size()];//getFlags(bean, association == null ? new ArrayList<Integer>() : association.getEvents());
        DictionaryEncodedColumn associatedPropertyColumn = null;
//        if (association != null && association.getColumn() != null) {
//            associatedPropertyColumn = segment.getColumn(new ColumnKey(association.getColumn())).getDictionaryEncodedColumn();
//        }
        TimeFilterInfo dayFilterBean = bean.getTimeFilter();
//        int dateStart = getDateStart(dayFilterBean.getTimeStart());
        int firstAssociatedIndex = -1;//(association == null || association.getEvents().size() == 0) ? -1 : association.getEvents().get(0);
        boolean repeated = step.hasRepeatedEvents();
        if (!repeated) {
            step = step.toNoRepeatedStep();
            return new GroupTWFilter(bean.getTimeWindow(), bean.getTimeGroup(), dayFilterBean,
                    step, firstAssociatedIndex, associatedProperty, associatedPropertyColumn);
        }
        return new TimeWindowFilter(bean.getTimeWindow(), dayFilterBean,
                step, firstAssociatedIndex, associatedProperty, associatedPropertyColumn);
    }

    private boolean[] getFlags(FunnelQueryBean bean, List<Integer> steps) {
        boolean[] flags = new boolean[bean.getAggregation().getFunnelEvents().size()];
        for (Integer step : steps) {
            flags[step] = true;
        }
        return flags;
    }

    private IStep createStep(List<FunnelEventBean> stepNames) {
        Set<FunnelEventBean> names = new HashSet<FunnelEventBean>();
        names.add(stepNames.get(0));
        boolean isHeadRepeated = false;
        boolean[][] steps = new boolean[stepNames.size()][eventDict.size()];
        for (int i = 0; i < stepNames.size(); i++) {
            FunnelEventBean eventBean = stepNames.get(i);
            if (i > 0 && names.contains(eventBean)) {
                isHeadRepeated = true;
            }
            List<String> eventStep = eventBean.getSteps();
            for (int j = 0; j < eventStep.size(); j++) {
                int index = eventDict.getIndex(eventStep.get(j));
                steps[i][index] = true;
            }
        }
        names.clear();
        names.addAll(stepNames);
        return new VirtualStep(steps, isHeadRepeated, names.size() < stepNames.size());
    }

}
