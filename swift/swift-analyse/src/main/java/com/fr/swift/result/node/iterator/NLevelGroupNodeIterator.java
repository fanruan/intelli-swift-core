package com.fr.swift.result.node.iterator;

import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.structure.iterator.Filter;
import com.fr.swift.structure.iterator.FilteredIterator;

import java.util.Iterator;

/**
 * 第N层node的迭代器
 *
 * Created by Lyon on 2018/4/11.
 */
public class NLevelGroupNodeIterator implements Iterator<GroupNode> {

    private Iterator<GroupNode> iterator;

    /**
     * -1层为根节点，依次类推
     *
     * @param depth
     * @param root
     */
    public NLevelGroupNodeIterator(final int depth, GroupNode root) {
        this.iterator = new FilteredIterator<GroupNode>(SwiftNodeUtils.dftNodeIterator(depth + 1, root), new Filter<GroupNode>() {
            @Override
            public boolean accept(GroupNode node) {
                return node.getDepth() == depth;
            }
        });
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public GroupNode next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
