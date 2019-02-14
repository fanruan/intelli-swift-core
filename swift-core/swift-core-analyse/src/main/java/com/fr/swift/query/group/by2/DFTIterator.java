package com.fr.swift.query.group.by2;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;

/**
 * 这个迭代器的默认行为是深度优先
 * <p>
 * Created by Lyon on 2018/4/23.
 */
public class DFTIterator implements Iterator<Pair<Integer, GroupByEntry>> {

    private LimitedStack<Iterator<GroupByEntry>> iterators;
    private IteratorCreator<GroupByEntry> creator;

    public DFTIterator(int limit, IteratorCreator<GroupByEntry> creator) {
        this.creator = creator;
        this.iterators = new ArrayLimitedStack<Iterator<GroupByEntry>>(limit);
        init();
    }

    private void init() {
        Iterator<GroupByEntry> iterator = creator.createIterator(0, null);
        iterators.push(iterator);
    }

    /**
     * 维度深度和该维度对应的索引，其中维度深度从0开始
     *
     * @return
     */
    private Pair<Integer, GroupByEntry> getNext() {
        Pair<Integer, GroupByEntry> ret = null;
        while (!iterators.isEmpty()) {
            Iterator<GroupByEntry> it = iterators.peek();
            if (it.hasNext()) {
                ret = Pair.of(iterators.size() - 1, it.next());
                if (iterators.size() != iterators.limit()) {
                    iterators.push(creator.createIterator(iterators.size(), ret.getValue()));
                }
                break;
            } else {
                // 树迭代器在迭代过程中的普通出栈方式
                iterators.pop();
            }
        }
        return ret;
    }

    @Override
    public boolean hasNext() {
        while (!iterators.isEmpty()) {
            if (!iterators.peek().hasNext()) {
                iterators.pop();
                continue;
            }
            return true;
        }
        return false;
    }

    @Override
    public Pair<Integer, GroupByEntry> next() {
        return getNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
