package com.fr.bi.cal.analyze.cal.result;

/**
 * Created by 小灰灰 on 2014/12/18.
 */
public class NodeAndPageInfo {
    private Node node;
    private boolean hasPre;
    private boolean hasNext;
    private int page;

    public NodeAndPageInfo(Node node, boolean hasPre, boolean hasNext, int page) {
        this.node = node;
        this.hasPre = hasPre;
        this.hasNext = hasNext;
        this.page = page;
    }

    public Node getNode() {
        return node;
    }

    public int isHasPre() {
        return hasPre ? 1 : 0;
    }

    public int isHasNext() {
        return hasNext ? 1 : 0;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getPage() {
        return page;

    }
}