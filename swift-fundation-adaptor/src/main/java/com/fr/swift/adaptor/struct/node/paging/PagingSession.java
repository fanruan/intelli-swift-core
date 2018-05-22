package com.fr.swift.adaptor.struct.node.paging;

/**
 * Created by Lyon on 2018/5/21.
 */
public class PagingSession {

    // 前一页的开始游标
    private int[] prevPageStartCursor = null;
    // 后一页的开始游标
    private int[] nextPageStartCursor = null;
    // 当前页的第一行
    private int[] firstRowOfPage = null;
    // 当前页的最后一行
    private int[] lastRowOfPage = null;

    public boolean hasNextPage() {
        return nextPageStartCursor != null;
    }

    public boolean hasPrevPage() {
        return prevPageStartCursor != null;
    }

    public int[] getPrevPageStartCursor() {
        return prevPageStartCursor;
    }

    public void setPrevPageStartCursor(int[] prevPageStartCursor) {
        this.prevPageStartCursor = prevPageStartCursor;
    }

    public int[] getNextPageStartCursor() {
        return nextPageStartCursor;
    }

    public void setNextPageStartCursor(int[] nextPageStartCursor) {
        this.nextPageStartCursor = nextPageStartCursor;
    }

    public int[] getFirstRowOfPage() {
        return firstRowOfPage;
    }

    public void setFirstRowOfPage(int[] firstRowOfPage) {
        this.firstRowOfPage = firstRowOfPage;
    }

    public int[] getLastRowOfPage() {
        return lastRowOfPage;
    }

    public void setLastRowOfPage(int[] lastRowOfPage) {
        this.lastRowOfPage = lastRowOfPage;
    }
}
