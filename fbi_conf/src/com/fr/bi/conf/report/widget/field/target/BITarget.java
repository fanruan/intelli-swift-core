package com.fr.bi.conf.report.widget.field.target;

import com.fr.bi.conf.report.style.TargetStyle;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.stable.report.result.TargetCalculator;

/**
 * Created by GUY on 2015/4/14.
 */
public interface BITarget extends BITargetAndDimension {

    TargetFilter getTargetFilter();

    int getTargetType();

    int getChartType();

    TargetCalculator createSummaryCalculator();

    TargetStyle getStyle();

    boolean calculateAllPage();
}