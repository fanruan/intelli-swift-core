package com.fr.swift.query.segment.group;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.compare.Comparators;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.funnel.IStep;
import com.fr.swift.query.aggregator.funnel.ITimeWindowFilter;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.StringColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class MergeIterator {
    private static final int MASK = ~(~0 << 16);
    private static final Comparator c = Comparators.STRING_ASC;

    private final ITimeWindowFilter filter;
    private final IStep step;
    private int postGroupIndex;

    private final Iterator<GroupByEntry>[] iterators;
    private final DictionaryEncodedColumn<String>[] recordDic;
    private final DetailColumn[] combineColumns;
    private final IMergeColumn[] associatedColumns;
    private final IMergeColumn[] groupColumns;
    private IntListRowTraversal[] travels;
    private String[] values;


    public MergeIterator(ITimeWindowFilter filter, IStep step, Iterator<GroupByEntry>[] iterators,
                         DictionaryEncodedColumn<String>[] recordDic, DetailColumn[] combineColumns,
                         DictionaryEncodedColumn[] associatedColumns, Column[] groupColumns,
                         int postGroupIndex) {
        this.filter = filter;
        this.step = step;
        this.iterators = iterators;
        this.recordDic = recordDic;
        SwiftLoggers.getLogger().debug("segment his recordDic size: {}", recordDic[0].size());
        if (recordDic[1] != null) {
            SwiftLoggers.getLogger().debug("segment append recordDic size: {}", recordDic[1].size());
        }
        this.combineColumns = combineColumns;
        this.associatedColumns = merge(associatedColumns);
        this.groupColumns = mergeGroupColumn(groupColumns);
        this.postGroupIndex = postGroupIndex;
        if (associatedColumns != null) {
            SwiftLoggers.getLogger().debug("associatedColumn size: {}", this.associatedColumns[0].dictSize());
            filter.setDictSize(this.associatedColumns[0].dictSize());
        }
        filter.init();

        travels = new IntListRowTraversal[2];
        values = new String[2];
        move(0);
        move(1);
    }

    private IMergeColumn[] mergeGroupColumn(Column[] groupColumns) {
        if (groupColumns == null) {
            return null;
        }
        if (groupColumns[0] instanceof StringColumn) {
            DictionaryEncodedColumn[] columns = new DictionaryEncodedColumn[2];
            columns[0] = groupColumns[0].getDictionaryEncodedColumn();
            columns[1] = groupColumns[1] == null ? null : groupColumns[1].getDictionaryEncodedColumn();
            return merge(columns);
        }
        return new IMergeColumn[]{new DetailOrigineColumn(groupColumns[0].getDetailColumn()),
                groupColumns[1] == null ? null : new DetailOrigineColumn(groupColumns[1].getDetailColumn())};
    }

    private void move(int index) {
        if (iterators[index].hasNext()) {
            GroupByEntry entry = iterators[index].next();
            values[index] = recordDic[index].getValue(entry.getIndex());
            travels[index] = (IntListRowTraversal) entry.getTraversal();
        } else {
            values[index] = null;
        }
    }

    public boolean hasNext() {
        return values[0] != null || values[1] != null;
    }


    public void record() {
        if (values[1] == null) {
            singleRecord(0);
            move(0);
        } else {
            if (values[0] == null) {
                singleRecord(1);
                move(1);
            } else {
                int result = c.compare(values[0], values[1]);
                if (result < 0) {
                    singleRecord(0);
                    move(0);
                } else if (result == 0) {
                    multiRecord();
                } else {
                    singleRecord(1);
                    move(1);
                }
            }
        }
    }

    private void singleRecord(final int i) {
        travels[i].traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                long combine = combineColumns[i].getLong(row);
                int timestamp = (int) (combine >> 32);
                int event = (int) (MASK & (combine >> 16));
                int dateIndex = (int) (MASK & combine);
                filter.add(event, timestamp, dateIndex,
                        associatedColumns == null ? -1 : associatedColumns[i].getIndex(row),
                        postGroupIndex == -1 ? null : step.isEqual(postGroupIndex, event) ? groupColumns[i].getValue(row) : null);
            }
        });
    }

    private int[] indexes = new int[]{0, 0};
    private int[] rows = new int[2];
    private long[] combines = new long[2];
    private IntList[] lists = new IntList[2];

    private void multiRecord() {
        indexes[0] = 0;
        indexes[1] = 0;
        lists[0] = travels[0].getList();
        lists[1] = travels[1].getList();
        rows[0] = lists[0].get(indexes[0]);
        rows[1] = lists[1].get(indexes[1]);
        combines[0] = combineColumns[0].getLong(rows[0]);
        combines[1] = combineColumns[1].getLong(rows[1]);
        while (indexes[0] < lists[0].size() || indexes[1] < lists[1].size()) {
            //不会相同的
            if (combines[0] < combines[1]) {
                filter(0);
            } else {
                filter(1);
            }
        }
        move(0);
        move(1);
    }

    private void filter(int i) {
        int timestamp = (int) (combines[i] >> 32);
        int event = (int) (MASK & (combines[i] >> 16));
        int dateIndex = (int) (MASK & combines[i]);
        filter.add(event, timestamp, dateIndex,
                associatedColumns == null ? -1 : associatedColumns[i].getIndex(rows[i]),
                postGroupIndex == -1 ? null : step.isEqual(postGroupIndex, event) ? groupColumns[i].getValue(rows[i]) : null);
        if (++indexes[i] < lists[i].size()) {
            rows[i] = lists[i].get(indexes[i]);
            combines[i] = combineColumns[i].getLong(rows[i]);
        } else {
            //设置成long.maxvalue，就不会遍历了，一直是大的那个
            combines[i] = Long.MAX_VALUE;
        }
    }

    private IMergeColumn[] merge(DictionaryEncodedColumn[] columns) {
        if (columns == null) {
            return null;
        }
        if (columns[0].size() < 1000) {
            return mergeSmallDic(columns);
        }
        DictionaryEncodedColumn dic0 = columns[0];
        DictionaryEncodedColumn dic1 = columns[1];
        Comparator c = dic0.getComparator();
        int index0 = 1;
        int index1 = 1;
        Object v0 = dic0.getValue(index0);
        Object v1 = dic1.getValue(index1);
        IntList mergeIndex0 = IntListFactory.createHeapIntList(dic0.size());
        mergeIndex0.add(0);
        IntList mergeIndex1 = IntListFactory.createHeapIntList(dic1.size());
        mergeIndex1.add(0);
        boolean hasDifferent = false;
        int currentIndex = 0;
        while (index0 < dic0.size() || index1 < dic1.size()) {
            currentIndex++;
            int result = c.compare(v0, v1);
            if (result == 0) {
                mergeIndex0.add(currentIndex);
                mergeIndex1.add(currentIndex);
                v0 = ++index0 < dic1.size() ? dic0.getValue(index0) : Comparators.MAX_INFINITY_STRING;
                v1 = ++index1 < dic1.size() ? dic1.getValue(index1) : Comparators.MAX_INFINITY_STRING;
            } else if (result < 0) {
                hasDifferent = true;
                mergeIndex0.add(currentIndex);
                v0 = ++index0 < dic0.size() ? dic0.getValue(index0) : Comparators.MAX_INFINITY_STRING;
            } else {
                mergeIndex1.add(currentIndex);
                v1 = ++index1 < dic1.size() ? dic1.getValue(index1) : Comparators.MAX_INFINITY_STRING;
            }
        }
        if (hasDifferent) {
            return new IMergeColumn[]{new MergeColumn(dic0, mergeIndex0), new MergeColumn(dic1, mergeIndex1)};
        } else {
            return new IMergeColumn[]{new OriginDecMergeColumn(dic0), new OriginDecMergeColumn(dic1)};
        }

    }

    private IMergeColumn[] mergeSmallDic(DictionaryEncodedColumn[] columns) {
        if (columns == null) {
            return null;
        }
        DictionaryEncodedColumn dic0 = columns[0];
        DictionaryEncodedColumn dic1 = columns[1];
        if (dic1 == null) {
            List values = new ArrayList();
            for (int i = 0; i < dic0.size(); i++) {
                values.add(dic0.getValue(i));
            }
            return new IMergeColumn[]{new ValueCachedOrigineColumn(columns[0], values), null};
        }

        Comparator c = dic0.getComparator();
        int index0 = 1;
        int index1 = 1;
        Object v0 = dic0.getValue(index0);
        Object v1 = dic1.getValue(index1);
        IntList mergeIndex0 = IntListFactory.createHeapIntList(dic0.size());
        mergeIndex0.add(0);
        IntList mergeIndex1 = IntListFactory.createHeapIntList(dic1.size());
        mergeIndex1.add(0);
        boolean hasDifferent = false;
        List values = new ArrayList();
        values.add(null);
        int currentIndex = 0;
        while (index0 < dic0.size() || index1 < dic1.size()) {
            currentIndex++;
            int result = c.compare(v0, v1);
            if (result == 0) {
                mergeIndex0.add(currentIndex);
                mergeIndex1.add(currentIndex);
                values.add(v0);
                v0 = ++index0 < dic0.size() ? dic0.getValue(index0) : Comparators.MAX_INFINITY_STRING;
                v1 = ++index1 < dic1.size() ? dic1.getValue(index1) : Comparators.MAX_INFINITY_STRING;
            } else if (result < 0) {
                hasDifferent = true;
                mergeIndex0.add(currentIndex);
                values.add(v0);
                v0 = ++index0 < dic0.size() ? dic0.getValue(index0) : Comparators.MAX_INFINITY_STRING;
            } else {
                hasDifferent = true;
                values.add(v1);
                mergeIndex1.add(currentIndex);
                v1 = ++index1 < dic1.size() ? dic1.getValue(index1) : Comparators.MAX_INFINITY_STRING;
            }
        }
        if (hasDifferent) {
            return new IMergeColumn[]{new ValueMergeColumn(dic0, mergeIndex0, values), new ValueMergeColumn(dic1, mergeIndex1, values)};
        } else {
            return new IMergeColumn[]{new ValueCachedOrigineColumn(dic0, values), new ValueCachedOrigineColumn(dic1, values)};
        }

    }
}
