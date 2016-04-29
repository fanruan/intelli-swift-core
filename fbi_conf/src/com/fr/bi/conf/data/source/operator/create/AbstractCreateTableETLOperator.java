package com.fr.bi.conf.data.source.operator.create;


import com.fr.bi.conf.data.source.operator.AbstractETLOperator;

/**
 * Created by 小灰灰 on 2014/8/7.
 */
public abstract class AbstractCreateTableETLOperator extends AbstractETLOperator {


    private static final long serialVersionUID = -1595028901140161690L;

    AbstractCreateTableETLOperator(long userId) {
        super(userId);
    }

    AbstractCreateTableETLOperator() {
    }

    @Override
    public boolean isAddColumnOprator() {
        return false;
    }
}