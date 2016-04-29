package com.fr.bi.cal.analyze.report.report.widget.chart.style;

import com.fr.base.Style;
import com.fr.bi.conf.report.style.TargetStyleConstant;
import com.fr.bi.conf.report.style.TargetTitle;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.chart.base.TextAttr;
import com.fr.chart.chartattr.Axis;
import com.fr.chart.chartattr.CategoryAxis;
import com.fr.chart.chartattr.Plot;
import com.fr.chart.chartglyph.DataSheet;
import com.fr.general.FRFont;
import com.fr.general.Inter;
import com.fr.json.JSONObject;

import java.awt.*;

public abstract class AbstractChartStyle extends SimpleChartStyle {

    protected boolean show_grid = true;
    private int classify_position = BIExcutorConstant.CHARTSTYLE.CLASSIFYDIRECTION_HORIZONTAL;
    private int data_sheet = BIExcutorConstant.CHARTSTYLE.HIDE_DATA_SHEET;
    private TargetTitle classify_title = new TargetTitle();

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("classify_direction")) {
            this.classify_position = jo.getInt("classify_direction");
        }
        if (jo.has("data_sheet")) {
            data_sheet = jo.getInt("data_sheet");
        }
        if (jo.has("show_grid")) {
            //1表示显示
            show_grid = jo.getInt("show_grid") == 1;
        }

        if (jo.has("classify_title")) {
            JSONObject titleJo = jo.getJSONObject("classify_title");
            classify_title.parseJSON(titleJo);
        }
    }

    @Override
    public void dealWithStyle(Plot plot) {
        super.dealWithStyle(plot);
        Axis xAxis = plot.getxAxis();
        if (plot != null && xAxis != null) {
            if (plot.getDataSheet() != null) {
                DataSheet dataSheet = plot.getDataSheet();
                dataSheet.setFormat(TargetStyleConstant.getFormat(format.createFormatString()));
                dataSheet.setVisible(data_sheet == BIExcutorConstant.CHARTSTYLE.SHOW_DATA_SHEET);
            }
            TextAttr ta = xAxis.getTextAttr();
            if (ta == null) {
                ta = new TextAttr();
                plot.getxAxis().setTextAttr(ta);
            }
            ta.setAlignText(classify_position == BIExcutorConstant.CHARTSTYLE.CLASSIFYDIRECTION_HORIZONTAL ?
                    Style.HORIZONTALTEXT : Style.VERTICALTEXT);
//            ta.setTitle()
            if (xAxis instanceof CategoryAxis) {
                xAxis.setTitle(classify_title.createTitle());
                if (xAxis.getTitle() != null) {
                    xAxis.getTitle().getTextAttr().setFRFont(FRFont.getInstance(Inter.getLocText("FR-Designer-All_MSBold"), Font.PLAIN, 8));
                }
            }
        }
    }
}