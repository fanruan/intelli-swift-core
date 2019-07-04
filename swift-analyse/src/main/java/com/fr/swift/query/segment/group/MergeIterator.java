package com.fr.swift.query.segment.group;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.funnel.IStep;
import com.fr.swift.query.funnel.TimeWindowFilter;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.StringColumn;
import com.fr.swift.structure.iterator.IntListRowTraversal;

import java.util.Iterator;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class MergeIterator {
    private final TimeWindowFilter filter;
    private final IStep step;
    private int postGroupIndex;

    private final Iterator<GroupByEntry> iterator;
    private final DictionaryEncodedColumn<String> recordDic;
    private final IMergeColumn associatedColumns;
    private final IMergeColumn groupColumns;
    private DetailColumn timestampDetails;
    private DictionaryEncodedColumn eventDicts;
    private IntListRowTraversal travels;
    private String values;

    public MergeIterator(TimeWindowFilter filter, IStep step, Iterator<GroupByEntry> iterator,
                         DictionaryEncodedColumn<String> recordDic, DetailColumn timestamp,
                         DictionaryEncodedColumn eventDicts,
                         DictionaryEncodedColumn associatedColumns, Column groupColumns,
                         int postGroupIndex) {
        this.filter = filter;
        this.step = step;
        this.iterator = iterator;
        this.recordDic = recordDic;
        SwiftLoggers.getLogger().debug("segment his recordDic size: {}", recordDic.size());
        this.timestampDetails = timestamp;
        this.eventDicts = eventDicts;
        this.associatedColumns = null == associatedColumns ? null : new OriginDecMergeColumn(associatedColumns);
        this.groupColumns = mergeGroupColumn(groupColumns);
        this.postGroupIndex = postGroupIndex;
        if (associatedColumns != null) {
            filter.setDictSize(this.associatedColumns.dictSize());
        }
        filter.init();

        travels = null;
        values = null;
        move();
    }

    private IMergeColumn mergeGroupColumn(Column groupColumns) {
        if (groupColumns == null) {
            return null;
        }
        if (groupColumns instanceof StringColumn) {
            return new OriginDecMergeColumn(groupColumns.getDictionaryEncodedColumn());
        } else {
            return new DetailOrigineColumn(groupColumns.getDetailColumn());
        }
    }

    private void move() {
        if (iterator.hasNext()) {
            GroupByEntry entry = iterator.next();
            values = recordDic.getValue(entry.getIndex());
            travels = (IntListRowTraversal) entry.getTraversal();
        } else {
            values = null;
        }
    }

    public boolean hasNext() {
        return values != null;
    }

    public void record() {
        singleRecord();
        move();
    }

    private void singleRecord() {
        travels.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                long timestamp = timestampDetails.getLong(row);
                int event = eventDicts.getIndexByRow(row);
                filter.add(event, timestamp,
                        associatedColumns == null ? -1 : associatedColumns.getIndex(row),
                        postGroupIndex == -1 ? null : step.isEqual(postGroupIndex, event) ? groupColumns.getValue(row) : null);
            }
        });
    }
}
