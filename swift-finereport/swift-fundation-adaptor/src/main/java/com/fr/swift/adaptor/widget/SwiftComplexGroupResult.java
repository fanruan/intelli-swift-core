package com.fr.swift.adaptor.widget;

import com.finebi.conf.structure.result.BIResult;
import com.finebi.conf.structure.result.table.BIComplexGroupResult;
import com.finebi.conf.structure.result.table.BIGroupNode;

import java.util.List;

/**
 * This class created on 2018/5/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftComplexGroupResult implements BIComplexGroupResult {
    private List<BIGroupNode> nodeList;

    public SwiftComplexGroupResult(List<BIGroupNode> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public int size() {
        return nodeList.size();
    }

    @Override
    public BIGroupNode getNode(int index) {
        return nodeList.get(index);
    }

    @Override
    public ResultType getResultType() {
        return BIResult.ResultType.BICOMPLEXGROUP;
    }
}