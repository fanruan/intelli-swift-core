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
import com.fr.swift.query.aggregator.funnel.impl.step.VirtualStep;
import com.fr.swift.query.filter.detail.impl.InFilter;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by.GroupByResult;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
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
    private FunnelQueryBean bean;
    private Segment segment;
    private Column stepName;
    private DictionaryEncodedColumn stepNameDicColumn;
    private final Map<String, Long> globalDic;
    private String[] dates;

    public FunnelSegmentQuery(Segment segment, FunnelQueryBean bean) {
        this.bean = bean;
        this.segment = segment;
        this.stepName = segment.getColumn(new ColumnKey(bean.getStepName()));
        this.stepNameDicColumn = stepName.getDictionaryEncodedColumn();
        this.globalDic = new HashMap<String, Long>();
        initGlobalDic();
        initDates();
    }

    private void initGlobalDic() {
        int dicSize = stepNameDicColumn.size();
        for (long i = 0; i < dicSize; i++) {
            String dicValue = String.valueOf(stepNameDicColumn.getValue((int) i));
            globalDic.put(dicValue, i);
        }
    }

    private void initDates() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        dates = new String[45];
        long start = 0;
        try {
            start = format.parse(bean.getDateStart()).getTime();
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
        Set<Integer> steps = getSteps(bean.getSteps());
        IStep step = createStep(bean.getSteps(), bean.isVirtualStep());
        ITimeWindowFilter filter = createTimeWindowFilter(step, bean, segment);
        Column idColumn = segment.getColumn(new ColumnKey(bean.getId()));
        RowTraversal rowTraversal = getFilerRows(idColumn.getBitmapIndex(), steps, bean, segment);
        SwiftLoggers.getLogger().debug("seg rows: {}", rowTraversal.getCardinality());
        DetailColumn combineColumn = segment.getColumn(new ColumnKey(bean.getCombine())).getDetailColumn();
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
                createAssociatedColumn(), createPostGroupColumn(), bean.getPostGroupStep());

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
        if (bean.getPostGroupName() == null) {
            return null;
        }
        Column a0 = segment.getColumn(new ColumnKey(bean.getPostGroupName()));
        return new Column[]{a0};
    }

    private DictionaryEncodedColumn[] createAssociatedColumn() {
        if (bean.getAssociatedProperty() == null) {
            return null;
        }
        DictionaryEncodedColumn a0 = segment.getColumn(new ColumnKey(bean.getAssociatedProperty())).getDictionaryEncodedColumn();
        return new DictionaryEncodedColumn[]{a0};
    }

    private FunnelGroupKey createGroupKey(int postGroupStep, int[][] rangePrices, IHead head) {
        FunnelGroupKey groupKey = null;
        Object groupValue = head.getGroupValue();
        if (postGroupStep != -1) {
            if (rangePrices.length == 0) {
                if (groupValue != null) {
                    groupKey = new FunnelGroupKey(head.getDate(), 0, (String) groupValue);
                } else {
                    groupKey = new FunnelGroupKey(head.getDate(), 0, null);
                }
            } else {
                Double price = (Double) groupValue;
                int priceGroup = -1;
                if (price != null) {
                    for (int j = 0; j < rangePrices.length; j++) {
                        if (price >= rangePrices[j][0] && price < rangePrices[j][1]) {
                            priceGroup = j;
                            break;
                        }
                    }
                }
                groupKey = new FunnelGroupKey(head.getDate(), priceGroup);
            }
        } else {
            groupKey = new FunnelGroupKey(head.getDate());
        }
        return groupKey;
    }

    private void aggregate(Map<FunnelGroupKey, FunnelAggValue> result, FunnelQueryBean bean, List<IHead> heads) {
        int numberOfSteps = bean.getSteps().length;
        boolean calMedian = bean.isCalMedian();
        int postGroupStep = bean.getPostGroupStep();

        for (IHead head : heads) {
            FunnelGroupKey groupKey = createGroupKey(postGroupStep, bean.getPostNumberRangeGroups(), head);
            FunnelAggValue contestAggValue = result.get(groupKey);
            if (contestAggValue == null) {
                int[] counter = new int[numberOfSteps];
                List<List<Integer>> lists = null;
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
        bitMap.and(createDateBitMap(segment, bean));

        return bitMap;
    }

    private ImmutableBitMap createDateBitMap(Segment segment, FunnelQueryBean bean) {
        Column dateColumn = segment.getColumn(new ColumnKey(bean.getDate()));
        Set<Object> values = new HashSet<Object>();
        String start = bean.getDateStart();
        int i = getDateStart(start);
        int numberOfDates = bean.getNumberOfDates();
        for (int j = 0; j < numberOfDates; j++) {
            values.add((long) (i + j));
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
        boolean[] associatedProperty = getFlags(bean, bean.getAssociatedSteps());
        DictionaryEncodedColumn associatedPropertyColumn = null;
        if (bean.getAssociatedProperty() != null) {
            associatedPropertyColumn = segment.getColumn(new ColumnKey(bean.getAssociatedProperty())).getDictionaryEncodedColumn();
        }
        int dateStart = getDateStart(bean.getDateStart());
        int firstAssociatedIndex = bean.getAssociatedSteps().length == 0 ? -1 : bean.getAssociatedSteps()[0];
        boolean repeated = step.hasRepeatedEvents();
        if (!repeated) {
            step = step.toNoRepeatedStep();
            return new GroupTWFilter(bean.getTimeWindow(), dateStart, bean.getNumberOfDates(),
                    step, firstAssociatedIndex, associatedProperty, associatedPropertyColumn);
        }
        return new TimeWindowFilter(bean.getTimeWindow(), dateStart, bean.getNumberOfDates(),
                step, firstAssociatedIndex, associatedProperty, associatedPropertyColumn);
    }

    private boolean[] getFlags(FunnelQueryBean bean, int[] steps) {
        boolean[] flags = new boolean[bean.getSteps().length];
        for (int i = 0; i < steps.length; i++) {
            flags[steps[i]] = true;
        }
        return flags;
    }

    private IStep createStep(String[][] stepNames, boolean hasVirtualEvent) {
        if (!hasVirtualEvent) {
            Set<String> names = new HashSet<String>();
            names.add(stepNames[0][0]);
            boolean isHeadRepeated = false;
            int[] steps = new int[stepNames.length];
            for (int i = 0; i < stepNames.length; i++) {
                if (i > 0 && names.contains(stepNames[i][0])) {
                    isHeadRepeated = true;
                }
                steps[i] = (int) (long) globalDic.get(stepNames[i][0]);
            }
            names.clear();
            for (String[] name : stepNames) {
                names.add(name[0]);
            }
            return new Step(steps, isHeadRepeated, names.size() < stepNames.length);
        }
        Set<String> names = new HashSet<String>(Arrays.asList(stepNames[0]));
        boolean isHeadRepeated = false;
        boolean[][] steps = new boolean[stepNames.length][];
        int total = 0;
        for (int i = 0; i < stepNames.length; i++) {
            steps[i] = new boolean[globalDic.size()];
            for (int j = 0; j < stepNames[i].length; j++) {
                if (i > 0 && names.contains(stepNames[i][j])) {
                    isHeadRepeated = true;
                }
                steps[i][(int) (long) globalDic.get(stepNames[i][j])] = true;
                total++;
            }
        }
        names.clear();
        for (String[] ns : stepNames) {
            names.addAll(Arrays.asList(ns));
        }
        return new VirtualStep(steps, isHeadRepeated, names.size() < total);
    }

    private Set<Integer> getSteps(String[][] stepNames) {
        Set<Integer> steps = new HashSet<Integer>();
        for (int i = 0; i < stepNames.length; i++) {
            for (String name : stepNames[i]) {
                steps.add((int) (long) globalDic.get(name));
            }
        }
        return steps;
    }
}
