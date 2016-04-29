package com.fr.bi.cal.analyze.report.report.widget.chart.setting.dashboard;

import com.fr.bi.cal.analyze.report.report.widget.chart.setting.pie.PIEOneTargetChart;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.DashBoardDefaultStyle;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.DashBoardStyle;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.chart.chartdata.MeterTableDefinition;
import com.fr.chart.chartdata.TopDefinition;
import com.fr.json.JSONObject;


public class DashboardOneTargetChart extends PIEOneTargetChart {

    @Override
    protected int getChartType() {
        return BIExcutorConstant.CHART.DASHBOARD;
    }

    /**
     * 转成json
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("style")) {
            style = new DashBoardStyle();
            style.parseJSON(jo.getJSONObject("style"));
        } else {
            style = new DashBoardDefaultStyle();
        }
    }

    @Override
    protected TopDefinition crateChartOneValueDefinition(BITarget summary,
                                                         String column) {
        MeterTableDefinition tableDataDef = new MeterTableDefinition();
        if (summary != null) {
            tableDataDef.setValue(summary.getValue());
        }

        if (column != null) {
            tableDataDef.setName(column);
        }
        return tableDataDef;
    }
}