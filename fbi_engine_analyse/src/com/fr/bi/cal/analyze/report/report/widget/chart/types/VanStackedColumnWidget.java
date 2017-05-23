package com.fr.bi.cal.analyze.report.report.widget.chart.types;

/**
 * Created by eason on 2017/2/27.
 */
public class VanStackedColumnWidget extends VanColumnWidget{

    protected boolean isStacked(String dimensionID){
        return true;
    }

    protected String getStackedKey(String dimensionID) {
        return this.getUsedTargetID()[0];
    }

}
