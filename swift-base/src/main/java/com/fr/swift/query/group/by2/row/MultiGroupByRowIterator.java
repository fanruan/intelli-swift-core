package com.fr.swift.query.group.by2.row;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by2.DFTIterator;
import com.fr.swift.query.group.by2.GroupByController;
import com.fr.swift.query.group.by2.ItCreator;
import com.fr.swift.query.group.by2.MultiGroupByV2;
import com.fr.swift.query.group.by2.PopUpCallback;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.BinaryFunction;

import java.util.Iterator;

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
        int dimensionSize = groupByInfo.getDimensions().size();
        final LimitedStack<GroupByEntry> itemsStack = new ArrayLimitedStack<GroupByEntry>(dimensionSize);
        DFTIterator iterator = new DFTIterator(dimensionSize, new ItCreator(groupByInfo), new PopUpCallback() {
            @Override
            public void popUp() {
                if (!itemsStack.isEmpty()) {
                    // 这个闭包比较恶心了
                    itemsStack.pop();
                }
            }
        });
        GroupByController<GroupByEntry> controller = new ExpandAllController();
        BinaryFunction<Integer, GroupByEntry, GroupByEntry> idFn = new BinaryFunction<Integer, GroupByEntry, GroupByEntry>() {
            @Override
            public GroupByEntry apply(Integer integer, GroupByEntry groupByEntry) {
                return groupByEntry;
            }
        };
        BinaryFunction<GroupByEntry, LimitedStack<GroupByEntry>, GroupByEntry[]> fn2 = new BinaryFunction<GroupByEntry, LimitedStack<GroupByEntry>, GroupByEntry[]>() {
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
