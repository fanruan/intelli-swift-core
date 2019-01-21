package com.fr.swift.query.segment.group;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.aggregator.funnel.IHead;
import com.fr.swift.query.aggregator.funnel.IStep;
import com.fr.swift.query.aggregator.funnel.ITimeWindowFilter;
import com.fr.swift.query.aggregator.funnel.impl.GroupTWFilter;
import com.fr.swift.query.aggregator.funnel.impl.TimeWindowFilter;
import com.fr.swift.query.aggregator.funnel.impl.step.Step;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.impl.InFilter;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by.GroupByResult;
import com.fr.swift.query.info.bean.element.aggregation.funnel.AssociationFilterBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.DayFilterBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.ParameterColumnsBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.PostGroupBean;
import com.fr.swift.query.info.bean.parser.FilterInfoParser;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.query.segment.SegmentQuery;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.funnel.FunnelQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.Crasher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelSegmentQuery implements SegmentQuery {

    // TODO: 2018/12/28
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    private FunnelQueryBean bean;
    private Segment segment;
    private DictionaryEncodedColumn eventDict;
    private final Map<String, Long> globalDic;
    private String[] dates;

    public FunnelSegmentQuery(Segment segment, FunnelQueryBean bean) {
        this.bean = bean;
        this.segment = segment;
        Column event = segment.getColumn(new ColumnKey(bean.getAggregation().getColumns().getEvent()));
        this.eventDict = event.getDictionaryEncodedColumn();
        this.globalDic = new HashMap<String, Long>();
        initGlobalDic();
        initDates();
    }

    private void initGlobalDic() {
        int dicSize = eventDict.size();
        // eventType不为空
        for (long i = 1; i < dicSize; i++) {
            String dicValue = String.valueOf(eventDict.getValue((int) i));
            globalDic.put(dicValue, i - 1);
        }
    }

    private void initDates() {
        dates = new String[45];
        long start = 0;
        try {
            start = format.parse(bean.getAggregation().getDayFilter().getDayStart()).getTime();
        } catch (ParseException e) {
            Crasher.crash(e);
        }
        for (int i = 0; i < 45; i++) {
            dates[i] = format.format(new Date(start));
            start += 24 * 60 * 60 * 1000;
        }
    }

    @Override
    public QueryResultSet getQueryResult() {
        Set<Integer> steps = getSteps(bean.getAggregation().getFunnelEvents());
        IStep step = createStep(bean.getAggregation().getFunnelEvents());
        ITimeWindowFilter filter = createTimeWindowFilter(step, bean, segment);
        ParameterColumnsBean params = bean.getAggregation().getColumns();
        Column event = segment.getColumn(new ColumnKey(params.getEvent()));
        RowTraversal rowTraversal = getFilerRows(event.getBitmapIndex(), steps, bean, segment);
        SwiftLoggers.getLogger().debug("seg rows: {}", rowTraversal.getCardinality());
        DetailColumn combineColumn = segment.getColumn(new ColumnKey(params.getCombine())).getDetailColumn();
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
                createAssociatedColumn(), createPostGroupColumn(), getPostGroupStep());

        Map<FunnelGroupKey, FunnelAggValue> results = new HashMap<FunnelGroupKey, FunnelAggValue>();

        while (mergeIterator.hasNext()) {
            mergeIterator.record();
            aggregate(results, bean, filter.getResult());
            filter.reset();
        }
        SwiftLoggers.getLogger().debug("segment result: {}", results.toString());
        return new FunnelQueryResultSet(new FunnelResultSet(results), null);
    }

    private Column[] createPostGroupColumn() {
        PostGroupBean groupBean = bean.getAggregation().getPostGroup();
        if (groupBean == null) {
            return null;
        }
        Column a0 = segment.getColumn(new ColumnKey(groupBean.getColumn()));
        return new Column[]{a0};
    }

    private DictionaryEncodedColumn[] createAssociatedColumn() {
        AssociationFilterBean associationFilterBean = bean.getAggregation().getAssociatedFilter();
        if (associationFilterBean == null) {
            return null;
        }
        DictionaryEncodedColumn a0 = segment.getColumn(new ColumnKey(associationFilterBean.getColumn())).getDictionaryEncodedColumn();
        return new DictionaryEncodedColumn[]{a0};
    }

    private FunnelGroupKey createGroupKey(int postGroupStep, List<double[]> rangePairs, IHead head) {
        FunnelGroupKey groupKey = null;
        Object groupValue = head.getGroupValue();
        if (postGroupStep != -1) {
            if (rangePairs.size() == 0) {
                if (groupValue != null) {
                    groupKey = new FunnelGroupKey(dates[head.getDate()], 0, (String) groupValue);
                } else {
                    groupKey = new FunnelGroupKey(dates[head.getDate()], 0, "");
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
                groupKey = new FunnelGroupKey(dates[head.getDate()], priceGroup, pair);
            }
        } else {
            groupKey = new FunnelGroupKey(dates[head.getDate()]);
        }
        return groupKey;
    }

    private boolean isCalMedian() {
        List<PostQueryInfoBean> beans = bean.getPostAggregations();
        for (PostQueryInfoBean postQueryInfoBean : beans) {
            if (postQueryInfoBean.getType() == PostQueryType.FUNNEL_MEDIAN) {
                return true;
            }
        }
        return false;
    }

    private int getPostGroupStep() {
        PostGroupBean groupBean = bean.getAggregation().getPostGroup();
        return groupBean == null ? -1 : groupBean.getFunnelIndex();
    }

    private void aggregate(Map<FunnelGroupKey, FunnelAggValue> result, FunnelQueryBean bean, List<IHead> heads) {
        int numberOfSteps = bean.getAggregation().getFunnelEvents().size();
        boolean calMedian = isCalMedian();
        int postGroupStep = getPostGroupStep();
        PostGroupBean groupBean = bean.getAggregation().getPostGroup();
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
            if (calMedian) {
                List<List<Integer>> lists = contestAggValue.getPeriods();
                int[] timestamps = head.getTimestamps();
                for (int i = 1; i < size; i++) {
                    lists.get(i - 1).add((timestamps[i] - timestamps[i - 1]));
                }
            }
        }
    }

    private List<List<Integer>> createList(int len) {
        List<List<Integer>> lists = new ArrayList<List<Integer>>();
        for (int i = 0; i < len; i++) {
            lists.add(new ArrayList<Integer>());
        }
        return lists;
    }

    private RowTraversal getFilerRows(BitmapIndexedColumn eventColumn, Set<Integer> steps, FunnelQueryBean bean,
                                      Segment segment) {
        MutableBitMap bitMap = BitMaps.newRoaringMutable();
        for (int index : steps) {
            bitMap.or(eventColumn.getBitMapIndex(index));
        }
        bitMap.and(FilterBuilder.buildDetailFilter(segment, FilterInfoParser.parse(new SourceKey(bean.getTableName()),
                bean.getFilter())).createFilterIndex());
        bitMap.and(createDateBitMap(segment, bean));

        return bitMap;
    }

    private ImmutableBitMap createDateBitMap(Segment segment, FunnelQueryBean bean) {
        DayFilterBean dayFilterBean = bean.getAggregation().getDayFilter();
        Column dateColumn = segment.getColumn(new ColumnKey(dayFilterBean.getColumn()));
        Set<Object> values = new HashSet<Object>();
        String start = dayFilterBean.getDayStart();
        int i = getDateStart(start);
        int numberOfDates = dayFilterBean.getNumberOfDays();
        for (int j = 0; j < numberOfDates; j++) {
            values.add(dates[i + j]);
        }
        InFilter filter = new InFilter(values, dateColumn);
        return filter.createFilterIndex();
    }

    private int getDateStart(String date) {
        int i = 0;
        for (; i < dates.length; i++) {
            if (dates[i].equals(date)) {
                break;
            }
        }
        return i;
    }

    private ITimeWindowFilter createTimeWindowFilter(IStep step, FunnelQueryBean bean, Segment segment) {
        AssociationFilterBean association = bean.getAggregation().getAssociatedFilter();
        boolean[] associatedProperty = getFlags(bean, association == null ? new ArrayList<Integer>() : association.getFunnelIndexes());
        DictionaryEncodedColumn associatedPropertyColumn = null;
        if (association != null && association.getColumn() != null) {
            associatedPropertyColumn = segment.getColumn(new ColumnKey(association.getColumn())).getDictionaryEncodedColumn();
        }
        DayFilterBean dayFilterBean = bean.getAggregation().getDayFilter();
        int dateStart = getDateStart(dayFilterBean.getDayStart());
        int firstAssociatedIndex = (association == null || association.getFunnelIndexes().size() == 0) ? -1 : association.getFunnelIndexes().get(0);
        boolean repeated = step.hasRepeatedEvents();
        if (!repeated) {
            step = step.toNoRepeatedStep();
            return new GroupTWFilter(bean.getAggregation().getTimeWindow(), dateStart, dayFilterBean.getNumberOfDays(),
                    step, firstAssociatedIndex, associatedProperty, associatedPropertyColumn);
        }
        return new TimeWindowFilter(bean.getAggregation().getTimeWindow(), dateStart, dayFilterBean.getNumberOfDays(),
                step, firstAssociatedIndex, associatedProperty, associatedPropertyColumn);
    }

    private boolean[] getFlags(FunnelQueryBean bean, List<Integer> steps) {
        boolean[] flags = new boolean[bean.getAggregation().getFunnelEvents().size()];
        for (int i = 0; i < steps.size(); i++) {
            flags[steps.get(i)] = true;
        }
        return flags;
    }

    private IStep createStep(List<String> stepNames) {
        Set<String> names = new HashSet<String>();
        names.add(stepNames.get(0));
        boolean isHeadRepeated = false;
        int[] steps = new int[stepNames.size()];
        for (int i = 0; i < stepNames.size(); i++) {
            if (i > 0 && names.contains(stepNames.get(i))) {
                isHeadRepeated = true;
            }
            steps[i] = (int) (long) globalDic.get(stepNames.get(i));
        }
        names.clear();
        names.addAll(stepNames);
        return new Step(steps, isHeadRepeated, names.size() < stepNames.size());
    }

    private Set<Integer> getSteps(List<String> stepNames) {
        Set<Integer> steps = new HashSet<Integer>();
        for (int i = 0; i < stepNames.size(); i++) {
            steps.add((int) (long) globalDic.get(stepNames.get(i)));
        }
        return steps;
    }
}
