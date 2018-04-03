package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BICrossLeftNode;
import com.finebi.conf.structure.result.table.BICrossNode;
import com.finebi.conf.structure.result.table.BIGroupNode;

/**
 * Created by Lyon on 2018/4/2.
 */
public class SwiftBICrossNode implements BICrossNode {

    @Override
    public BICrossLeftNode getCrossLeftNode() {
        return null;
    }

    @Override
    public BIGroupNode getTop() {
        return null;
    }

    @Override
    public boolean needIgnore() {
        return false;
    }

    @Override
    public ResultType getResultType() {
        return null;
    }
}
