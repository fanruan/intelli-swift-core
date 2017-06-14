package com.fr.bi.cal.analyze.cal.result.operator;

import com.fr.bi.cal.analyze.cal.sssecret.GroupConnectionValue;

public class AllPageOperator extends NextPageOperator {

    public AllPageOperator() {
        super(-1);
    }

    @Override
    public boolean isPageEnd(GroupConnectionValue gc) {
        return false;
    }

}