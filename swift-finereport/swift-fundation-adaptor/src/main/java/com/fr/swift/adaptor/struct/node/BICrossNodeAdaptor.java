package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BICrossLeftNode;
import com.finebi.conf.structure.result.table.BICrossNode;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.result.node.xnode.XGroupNode;

/**
 * Created by Lyon on 2018/4/2.
 */
public class BICrossNodeAdaptor implements BICrossNode {

    private XGroupNode xGroupNode;

    public BICrossNodeAdaptor(XGroupNode xGroupNode) {
        this.xGroupNode = xGroupNode;
    }

    @Override
    public BICrossLeftNode getCrossLeftNode() {
        return new BICrossLeftNodeAdaptor(xGroupNode.getCrossLeftNode());
    }

    @Override
    public BIGroupNode getTop() {
        return new BIGroupNodeAdaptor(xGroupNode.getTopGroupNode());
    }

    @Override
    public boolean needIgnore() {
        return false;
    }

}
