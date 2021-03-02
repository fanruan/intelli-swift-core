package com.fr.swift.cloud.result.node.iterator;

import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeUtils;
import com.fr.swift.cloud.structure.iterator.Filter;
import com.fr.swift.cloud.structure.iterator.FilteredIterator;

import java.util.Iterator;

/**
 * 第N层node的迭代器
 *
 * Created by Lyon on 2018/4/11.
 */
public class NLevelGroupNodeIterator implements Iterator<SwiftNode> {

    private Iterator<SwiftNode> iterator;

    /**
     * -1层为根节点，依次类推
     *
     * @param depth
     * @param root
     */
    public NLevelGroupNodeIterator(final int depth, SwiftNode root) {
        this.iterator = new FilteredIterator<SwiftNode>(SwiftNodeUtils.dftNodeIterator(depth + 1, root), new Filter<SwiftNode>() {
            @Override
            public boolean accept(SwiftNode node) {
                return node.getDepth() == depth;
            }
        });
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public SwiftNode next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
