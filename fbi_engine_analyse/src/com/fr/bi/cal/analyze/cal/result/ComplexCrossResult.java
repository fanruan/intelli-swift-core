package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.report.result.BIComplexCrossResult;
import com.fr.bi.report.result.BICrossNode;

import java.util.Map;

/**
 * Created by andrew_asa on 2017/8/4.
 */
public class ComplexCrossResult implements BIComplexCrossResult {

    private Map<Integer, XNode[]> nodes;

    private int size;

    public ComplexCrossResult(Map<Integer, XNode[]> nodes) {

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
    public BICrossNode[] getNode(int index) {

        if (nodes != null) {
            return nodes.get(new Integer(index));
        }
        return null;
    }

}
