package com.fr.swift.cloud.query.aggregator.funnel;

import com.fr.swift.cloud.bitmap.traversal.TraversalAction;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.group.by.GroupByEntry;
import com.fr.swift.cloud.query.query.funnel.IStep;
import com.fr.swift.cloud.query.query.funnel.TimeWindowFilter;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.segment.column.impl.StringColumn;
import com.fr.swift.cloud.structure.iterator.IntListRowTraversal;

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
    private DictionaryEncodedColumn eventDict;
    private IntListRowTraversal travels;
    private String values;

    public MergeIterator(TimeWindowFilter filter, IStep step, Iterator<GroupByEntry> iterator,
                         DictionaryEncodedColumn<String> recordDic, DetailColumn timestamp,
                         DictionaryEncodedColumn eventDict,
                         DictionaryEncodedColumn associatedColumns, Column groupColumns,
                         int postGroupIndex) {
        this.filter = filter;
        this.step = step;
        this.iterator = iterator;
        this.recordDic = recordDic;
        SwiftLoggers.getLogger().debug("segment his recordDic size: {}", recordDic.size());
        this.timestampDetails = timestamp;
        this.eventDict = eventDict;
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
            return new DetailOriginColumn(groupColumns.getDetailColumn());
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
                int event = eventDict.getIndexByRow(row);
                filter.add(event, timestamp,
                        associatedColumns == null ? -1 : associatedColumns.getIndex(row),
                        postGroupIndex == -1 ? null : step.isEqual(postGroupIndex, event, row) ? groupColumns.getValue(row) : null, row);
            }
        });
    }
}
