package com.fr.bi.cal.analyze.report.report.widget.chart.style;

import com.fr.bi.conf.report.style.TargetFormat;
import com.fr.bi.conf.report.style.TargetStyleConstant;
import com.fr.bi.conf.report.style.TargetTitle;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.chart.base.AttrContents;
import com.fr.chart.base.ChartConstants;
import com.fr.chart.base.TextAttr;
import com.fr.chart.chartattr.PiePlot;
import com.fr.chart.chartattr.Plot;
import com.fr.chart.chartglyph.ConditionAttr;
import com.fr.general.FRFont;
import com.fr.json.JSONObject;
import com.fr.stable.Constants;

import java.awt.*;

public class SimpleChartStyle implements ChartStyle {
    protected boolean showDataPointLabel;
    protected TargetFormat format = new TargetFormat();
    protected TargetTitle title = new TargetTitle();
    private int series_position = BIExcutorConstant.CHARTSTYLE.SERICEPOSITION_DOWN;

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("series_position")) {
            this.series_position = jo.getInt("series_position");
        }
        if (jo.has("showDataPointLabel")) {
            this.showDataPointLabel = jo.getBoolean("showDataPointLabel");
        }
        if (jo.has("format")) {
            format = new TargetFormat();
            format.parseJSON(jo.getJSONObject("format"));
        }
        if (jo.has("axis_title")) {
            title.parseJSON(jo.getJSONObject("axis_title"));
        }
    }

    @Override
    public void dealWithStyle(Plot plot) {
        if (plot == null) {
            return;
        }
        if (plot.getLegend() != null) {
            plot.getLegend().setPosition(series_position == BIExcutorConstant.CHARTSTYLE.SERICEPOSITION_DOWN
                    ? Constants.BOTTOM : Constants.RIGHT);
        }
        if (this.showDataPointLabel) {
            ConditionAttr attr = plot.getConditionCollection().getDefaultAttr();
            AttrContents attrContents = new AttrContents();
            FRFont font = FRFont.getInstance("Arial", Font.PLAIN, 8);
            attrContents.setFormat(TargetStyleConstant.getFormat(format.createFormatString()));
            attrContents.setTextAttr(new TextAttr(font));
            attrContents.setSeriesLabel(ChartConstants.VALUE_PARA);
            attrContents.setPosition(plot instanceof PiePlot ? Constants.INSIDE : Constants.OUTSIDE);
            attr.addDataSeriesCondition(attrContents);
        }
        if (plot != null && plot.getHotTooltipStyle() != null) {
            plot.getHotTooltipStyle().setFormat(TargetStyleConstant.getFormat(format.createFormatString()));
        }
    }
}