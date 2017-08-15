package com.fr.bi.report.result.imp;

import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.report.result.BIComplexGroupResult;
import com.fr.bi.report.result.BIGroupNode;

import java.util.Map;

/**
 * Created by andrew_asa on 2017/8/4.
 */
public class ComplexGroupResult implements BIComplexGroupResult {

    private Map<Integer, Node> nodes;

    private int size;

    public ComplexGroupResult(Map<Integer, Node> nodes) {

        if (nodes != null) {
            this.nodes = nodes;
            this.size = nodes.size();
        }
    }

    @Override
    public int size() {

        return size;
    }

    @Override
    public BIGroupNode getNode(int index) {

        if (nodes != null && index < size) {

            return nodes.get(new Integer(index));
        }
        return null;
    }

}
