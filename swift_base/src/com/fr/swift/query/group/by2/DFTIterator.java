package com.fr.swift.query.group.by2;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;

/**
 * 这个迭代器的默认行为是深度优先，但不排除GroupByController通过调用PopUpCallback#pop()跳过（过滤）一些节点
 *
 * Created by Lyon on 2018/4/23.
 */
public class DFTIterator implements Iterator<GroupByEntry>, PopUpCallback {

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

    private GroupByEntry getNext() {
        GroupByEntry ret = null;
        while (!iterators.isEmpty()) {
            Iterator<GroupByEntry> it = iterators.peek();
            if (it.hasNext()) {
                ret = it.next();
                if (iterators.size() != iterators.limit()) {
                    iterators.push(creator.createIterator(iterators.size(), ret));
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
        if (iterators.isEmpty()) {
            return false;
        }
        if (iterators.peek().hasNext()) {
            return true;
        } else {
            iterators.pop();
            return hasNext();
        }
    }

    @Override
    public GroupByEntry next() {
        return getNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void pop() {
        // 通过被控制器调用的出栈方式，用于控制groupBy的展开方式
        if (iterators.size() > 1) {
            // LAZY_EXPANDER至少展开一个维度
            iterators.pop();
        }
    }
}
