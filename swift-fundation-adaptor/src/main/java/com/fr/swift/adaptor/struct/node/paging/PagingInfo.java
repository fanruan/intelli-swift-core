package com.fr.swift.adaptor.struct.node.paging;

import com.fr.swift.query.adapter.dimension.Expander;

/**
 * Created by Lyon on 2018/5/20.
 */
public class PagingInfo {

    private boolean isNextPage;
    private int pageSize;
    private Expander expander;

    public PagingInfo(boolean isNextPage, int pageSize, Expander expander) {
        this.isNextPage = isNextPage;
        this.pageSize = pageSize;
        this.expander = expander;
    }

    public boolean isNextPage() {
        return isNextPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Expander getExpander() {
        return expander;
    }
}
