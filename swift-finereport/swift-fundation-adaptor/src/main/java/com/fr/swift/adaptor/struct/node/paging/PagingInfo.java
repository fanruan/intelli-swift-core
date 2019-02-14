package com.fr.swift.adaptor.struct.node.paging;

import com.fr.swift.query.group.info.cursor.Expander;

/**
 * Created by Lyon on 2018/5/20.
 */
public class PagingInfo {

    private boolean isFirstPage;
    private boolean isNextPage;
    private int pageSize;
    private String pagingSessionId;
    private Expander expander;

    public PagingInfo(boolean isFirstPage, boolean isNextPage, int pageSize, String pagingSessionId, Expander expander) {
        this.isFirstPage = isFirstPage;
        this.isNextPage = isNextPage;
        this.pageSize = pageSize;
        this.pagingSessionId = pagingSessionId;
        this.expander = expander;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public boolean isNextPage() {
        return isNextPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getPagingSessionId() {
        return pagingSessionId;
    }

    public Expander getExpander() {
        return expander;
    }
}
