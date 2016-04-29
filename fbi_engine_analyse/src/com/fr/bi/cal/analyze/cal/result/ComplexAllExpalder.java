package com.fr.bi.cal.analyze.cal.result;

/**
 * Created by 小灰灰 on 2014/12/24.
 */
public class ComplexAllExpalder extends ComplexExpander {

    @Override
    public NodeExpander getXExpander(int regionIndex) {
        return NodeExpander.ALL_EXPANDER;

    }

    @Override
    public NodeExpander getYExpander(int regionIndex) {
        return NodeExpander.ALL_EXPANDER;
    }
}