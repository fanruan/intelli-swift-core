package com.fr.bi.cal.analyze.report.report.widget.chart.setting.axes;

import com.fr.bi.cal.analyze.report.report.widget.chart.setting.tiao.TiaoOneTargetChart;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.BIMutileAxisChartStyle;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.json.JSONObject;


public class AxesOneTargetChart extends TiaoOneTargetChart {

    @Override
    protected int getChartType(BISummaryTarget target) {
        if (target == null) {
            return BIExcutorConstant.CHART.BAR;
        }
        return target.getChartType();
    }

    //FIXME 数据结构有问题 待删除
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("style")) {
            style = new BIMutileAxisChartStyle();
            style.parseJSON(jo.getJSONObject("style"));
        }
    }

}