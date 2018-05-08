package com.fr.swift.query.group.by2;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;

/**
 * 这个迭代器的默认行为是深度优先，但不排除GroupByController通过调用PopUpCallback#popUp()跳过（过滤）一些节点
 *
 * Created by Lyon on 2018/4/23.
 */
public class DFTIterator implements Iterator<GroupByEntry>, PopUpCallback {

    private LimitedStack<Iterator<GroupByEntry>> iterators;
    private IteratorCreator<GroupByEntry> creator;
    private PopUpCallback itemPopUp;

    public DFTIterator(int limit, IteratorCreator<GroupByEntry> creator, PopUpCallback itemPopUp) {
        this.creator = creator;
        this.iterators = new ArrayLimitedStack<Iterator<GroupByEntry>>(limit);
        this.itemPopUp = itemPopUp;
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
        boolean hasNext = false;
        while (!iterators.isEmpty()) {
            if (!iterators.peek().hasNext()) {
                iterators.pop();
                // 好恶心啊！ 这里也要让items的stack出栈。比如[a, a5]到[b, null]的过程
                itemPopUp.popUp();
                continue;
            }
            hasNext = true;
            break;
        }
        return hasNext;
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
    public void popUp() {
        // 通过被控制器调用的出栈方式，用于控制groupBy的展开方式
        if (iterators.size() > 1) {
            // LAZY_EXPANDER至少展开一个维度
            iterators.pop();
        }
    }
}
