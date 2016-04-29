package com.fr.bi.cal.analyze.cal.result.operator;


import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.cal.sssecret.IRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

public class LastPageOperator extends AbstractOperator {

    public LastPageOperator(int maxRow) {
        super(maxRow);
    }

    @Override
    public NodeDimensionIterator[] getPageIterator(IRootDimensionGroup[] roots,
                                                   NodeExpander expander) {
        NodeDimensionIterator[] iters = new NodeDimensionIterator[roots.length];
        for (int i = 0; i < roots.length; i++) {
            roots[i].setExpander(expander);
            iters[i] = roots[i].moveLast();
        }
        return iters;
    }

}