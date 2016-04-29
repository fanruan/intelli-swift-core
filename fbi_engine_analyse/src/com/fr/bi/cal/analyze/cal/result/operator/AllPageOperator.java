package com.fr.bi.cal.analyze.cal.result.operator;

public class AllPageOperator extends NextPageOperator {

    public AllPageOperator() {
        super(-1);
    }

    @Override
    public boolean isPageEnd() {
        return false;
    }

}