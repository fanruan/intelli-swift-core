package com.fr.bi.cal.analyze.report.report.widget.chart.types;

/**
 * Created by eason on 2017/3/20.
 */
public class VanStackedAreaWidget extends VanStackedColumnWidget{

    public String getSeriesType(String dimensionID){
        return "area";
    }

    protected boolean checkValid(){
        return this.getDim1Size() > 0 && this.hasTarget();
    }

}
