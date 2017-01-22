package com.fr.bi.cal.analyze.cal.result.operator;


import com.fr.bi.cal.analyze.cal.sssecret.IRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

public class LastPageOperator extends AbstractOperator {

    public LastPageOperator(int maxRow) {
        super(maxRow);
    }

    @Override
    public NodeDimensionIterator getPageIterator(IRootDimensionGroup root) {
        return root.moveLast();
    }

}