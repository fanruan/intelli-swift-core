package com.fr.bi.cal.analyze.report.data.drill;

/**
 * Created by GUY on 2015/4/8.
 */
public class Filter4Drill {
    public FilterDimensionForDrill filter4Drill;
    public String calTargetName;
    public String widgetName;
    public String targetName;
    public boolean isCalTarget = false;

    public Filter4Drill(FilterDimensionForDrill filter4Drill, String calTargetName, String widgetName, String targetName, boolean isCalTarget) {
        this.filter4Drill = filter4Drill;
        this.calTargetName = calTargetName;
        this.widgetName = widgetName;
        this.targetName = targetName;
        this.isCalTarget = isCalTarget;
    }
}