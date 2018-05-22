package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.structure.result.table.BITableResult;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.page.NodeRange;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.Tree2RowIterator;

import java.util.List;

/**
 * Created by Lyon on 2018/5/10.
 */
public class SwiftTableResult implements BITableResult {

    private boolean hasNextPage;
    private boolean hasPrevPage;
    private BIGroupNode node;

    public SwiftTableResult(boolean hasNextPage, boolean hasPrevPage, BIGroupNode node) {
        this.hasNextPage = hasNextPage;
        this.hasPrevPage = hasPrevPage;
        this.node = node;
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
}
