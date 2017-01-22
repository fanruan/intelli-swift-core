package com.fr.bi.cal.analyze.cal.result.operator;


import com.fr.bi.cal.analyze.cal.sssecret.IRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

public class NextPageOperator extends AbstractOperator {

    public NextPageOperator(int maxRow) {
        super(maxRow);
    }

    @Override
    public NodeDimensionIterator getPageIterator(IRootDimensionGroup root) {
        return root.moveNext();
    }

}