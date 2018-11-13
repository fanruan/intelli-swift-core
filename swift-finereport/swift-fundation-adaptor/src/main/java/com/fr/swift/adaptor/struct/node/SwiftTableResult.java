package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.structure.result.table.BITableResult;
import com.fr.swift.adaptor.widget.datamining.DMErrorWrap;

/**
 * Created by Lyon on 2018/5/10.
 */
public class SwiftTableResult implements BITableResult {

    private boolean hasNextPage;
    private boolean hasPrevPage;
    private BIGroupNode node;
    private DMErrorWrap errorWrap;

    public SwiftTableResult(boolean hasNextPage, boolean hasPrevPage, BIGroupNode node, DMErrorWrap errorWrap) {
        this.hasNextPage = hasNextPage;
        this.hasPrevPage = hasPrevPage;
        this.node = node;
        this.errorWrap = errorWrap;
    }

    @Override
    public BIGroupNode getNode() {
        return node;
    }

    @Override
    public boolean hasNextPage() {
        return hasNextPage;
    }

    @Override
    public boolean hasPreviousPage() {
        return hasPrevPage;
    }

    @Override
    public ResultType getResultType() {
        return ResultType.BIGROUP;
    }

    @Override
    public String getDataMiningError() {
        return null;
    }
}
