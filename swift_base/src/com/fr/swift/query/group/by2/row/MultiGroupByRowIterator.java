package com.fr.swift.query.group.by2.row;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by2.DFTIterator;
import com.fr.swift.query.group.by2.GroupByController;
import com.fr.swift.query.group.by2.ItCreator;
import com.fr.swift.query.group.by2.MultiGroupByV2;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.Function2;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/27.
 */
public class MultiGroupByRowIterator implements Iterator<GroupByEntry[]> {

    private GroupByInfo groupByInfo;
    private Iterator<GroupByEntry[]> iterator;

    public MultiGroupByRowIterator(GroupByInfo groupByInfo) {
        this.groupByInfo = groupByInfo;
        init();
    }

    private void init() {
        List<Column> dimensions = groupByInfo.getDimensions();
        DFTIterator iterator = new DFTIterator(dimensions.size(), new ItCreator(groupByInfo));
        LimitedStack<GroupByEntry> itemsStack = new ArrayLimitedStack<GroupByEntry>(dimensions.size());
        GroupByController<GroupByEntry> controller = new ExpandAllController();
        Function2<Integer, GroupByEntry, GroupByEntry> idFn = new Function2<Integer, GroupByEntry, GroupByEntry>() {
            @Override
            public GroupByEntry apply(Integer integer, GroupByEntry groupByEntry) {
                return groupByEntry;
            }
        };
        Function2<GroupByEntry, LimitedStack<GroupByEntry>, GroupByEntry[]> fn2 = new Function2<GroupByEntry, LimitedStack<GroupByEntry>, GroupByEntry[]>() {
            @Override
            public GroupByEntry[] apply(GroupByEntry groupByEntry, LimitedStack<GroupByEntry> groupByEntryLimitedStack) {
                return groupByEntryLimitedStack.toList().toArray(new GroupByEntry[groupByEntryLimitedStack.limit()]);
            }
        };
        this.iterator = new MultiGroupByV2<GroupByEntry>(iterator, itemsStack, controller, idFn, fn2);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public GroupByEntry[] next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
