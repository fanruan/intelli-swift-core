package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

/**
 * Created by 小灰灰 on 2014/12/18.
 */
public class NodeAndPageInfo {
    private Node node;
    private NodeDimensionIterator iterator;
    private boolean hasNext;

    public NodeAndPageInfo(Node node, NodeDimensionIterator iterator) {
        this.node = node;
        this.iterator = iterator;
        this.hasNext = iterator.hasNext();
    }

    public Node getNode() {
        return node;
    }

    public NodeDimensionIterator getIterator() {
        return iterator;
    }

    public int isHasPre() {
        return iterator.hasPrevious() ? 1 : 0;
    }

    public int isHasNext() {
        return hasNext ? 1 : 0;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getPage() {
        return iterator.getPageIndex();
    }
}