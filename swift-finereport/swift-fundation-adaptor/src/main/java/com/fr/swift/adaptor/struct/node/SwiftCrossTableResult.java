package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BICrossNode;
import com.finebi.conf.structure.result.table.BICrossTableResult;

/**
 * TODO
 * Created by Lyon on 2018/5/10.
 */
public class SwiftCrossTableResult implements BICrossTableResult {

    private static int LEFT_PAGE_SIZE = 20;
    private static int TOP_PAGE_SIZE = 8;

    @Override
    public BICrossNode getNode() {
        return null;
    }

    @Override
    public boolean hasHorizontalNextPage() {
        return false;
    }

    @Override
    public boolean hasHorizontalPreviousPage() {
        return false;
    }

    @Override
    public boolean hasVerticalNextPage() {
        return false;
    }

    @Override
    public boolean hasVerticalPreviousPage() {
        return false;
    }

    @Override
    public ResultType getResultType() {
        return ResultType.BICROSS;
    }

    @Override
    public String getDataMiningError() {
        return null;
    }
}
