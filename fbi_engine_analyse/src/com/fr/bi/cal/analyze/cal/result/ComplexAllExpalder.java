package com.fr.bi.cal.analyze.cal.result;

import java.io.Serializable;

/**
 * Created by 小灰灰 on 2014/12/24.
 */
public class ComplexAllExpalder extends ComplexExpander implements Serializable{

    private static final long serialVersionUID = -8433093486752591553L;

    @Override
    public NodeExpander getXExpander(int regionIndex) {
        return NodeExpander.ALL_EXPANDER;

    }

    @Override
    public NodeExpander getYExpander(int regionIndex) {
        return NodeExpander.ALL_EXPANDER;
    }
}