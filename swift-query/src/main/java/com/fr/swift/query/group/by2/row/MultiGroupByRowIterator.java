package com.fr.swift.query.group.by2.row;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by2.DFTIterator;
import com.fr.swift.query.group.by2.ItCreator;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.structure.Pair;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/27.
 */
public class MultiGroupByRowIterator implements Iterator<GroupByEntry[]> {

    private Iterator<GroupByEntry[]> iterator;

    public MultiGroupByRowIterator(GroupByInfo groupByInfo) {
        this.iterator = new RowIterator(groupByInfo);
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

    private static class RowIterator implements Iterator<GroupByEntry[]> {

        private int dimensionSize;
        private Iterator<Pair<Integer, GroupByEntry>> iterator;
        private GroupByEntry[] cachedEntries;

        public RowIterator(GroupByInfo groupByInfo) {
            this.dimensionSize = groupByInfo.getDimensions().size();
            this.iterator = new DFTIterator(dimensionSize, new ItCreator(groupByInfo));
            this.cachedEntries = new GroupByEntry[dimensionSize];
        }

        private GroupByEntry[] getRow() {
            GroupByEntry[] ret = null;
            while (iterator.hasNext()) {
                Pair<Integer, GroupByEntry> pair = iterator.next();
                int index = pair.getKey();
                cachedEntries[index] = pair.getValue();
                if (index == dimensionSize - 1) {
                    // 拷贝一下引用
                    ret = Arrays.copyOf(cachedEntries, dimensionSize);
                    break;
                }
            }
            return ret;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public GroupByEntry[] next() {
            return getRow();
        }

        @Override
        public void remove() {
        }
    }
}
