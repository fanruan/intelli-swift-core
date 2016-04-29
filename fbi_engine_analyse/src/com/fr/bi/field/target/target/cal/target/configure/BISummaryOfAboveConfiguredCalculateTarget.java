package com.fr.bi.field.target.target.cal.target.configure;


import com.fr.bi.field.target.calculator.cal.configure.SumOfAboveCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;

public class BISummaryOfAboveConfiguredCalculateTarget extends
        BIConfiguredCalculateTarget {


    @Override
    public TargetCalculator createSummaryCalculator() {
        return new SumOfAboveCalculator(this, getCalTargetName(), getStart_group());
    }
}