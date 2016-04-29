package com.fr.bi.cal.analyze.report.report.widget.chart.style;

import com.fr.base.Style;
import com.fr.bi.conf.report.style.TargetStyleConstant;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.chart.base.ChartConstants;
import com.fr.chart.chartattr.Axis;
import com.fr.chart.chartattr.CategoryAxis;
import com.fr.chart.chartattr.NumberAxis;
import com.fr.chart.chartattr.Plot;
import com.fr.general.FRFont;
import com.fr.general.Inter;
import com.fr.json.JSONObject;
import com.fr.stable.Constants;

import java.awt.*;

public class BISingleAxisChartStyle extends AbstractChartStyle {

    private int value_unit = BIExcutorConstant.CHARTSTYLE.VALUE_UNIT_NONE;

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("value_unit")) {
            this.value_unit = jo.getInt("value_unit");
        }
    }

    private void dealWithNumberAxis(NumberAxis axis, Plot plot) {
        NumberAxis na = axis;
        axis.setFormat(TargetStyleConstant.getFormat(format.createFormatString()));
        axis.setMainGridStyle(show_grid ? Constants.LINE_THIN : Constants.LINE_NONE);
        axis.setTitle(title.createTitle());
        if (axis.getTitle() != null) {
            axis.getTitle().getTextAttr().setFRFont(FRFont.getInstance(Inter.getLocText("FR-Designer-All_MSBold"), Font.PLAIN, 9));
        }

        switch (value_unit) {
            case BIExcutorConstant.CHARTSTYLE.VALUE_UNIT_NONE: {
                break;
            }
            case BIExcutorConstant.CHARTSTYLE.VALUE_UNIT_TEN_THOUSAND: {
                na.setShowUnit(ChartConstants.UNIT_I18N_KEYS[3]);
                plot.setShowUnit(ChartConstants.UNIT_I18N_KEYS[3]);
                break;
            }
            case BIExcutorConstant.CHARTSTYLE.VALUE_UNIT_MILLION: {
                na.setShowUnit(ChartConstants.UNIT_I18N_KEYS[5]);
                plot.setShowUnit(ChartConstants.UNIT_I18N_KEYS[5]);
                break;
            }
            case BIExcutorConstant.CHARTSTYLE.VALUE_UNIT_HUNDRED_MILLION: {
                na.setShowUnit(ChartConstants.UNIT_I18N_KEYS[7]);
                plot.setShowUnit(ChartConstants.UNIT_I18N_KEYS[7]);
                break;
            }
        }
    }

    @Override
    public void dealWithStyle(Plot plot) {
        super.dealWithStyle(plot);
        if (plot != null) {
            Axis axis = plot.getyAxis();
            if (axis instanceof NumberAxis) {
                dealWithNumberAxis((NumberAxis) axis, plot);
            }
            axis = plot.getxAxis();
            if (axis instanceof NumberAxis) {
                dealWithNumberAxis((NumberAxis) axis, plot);
                if (axis.getTitle() != null) {
                    axis.getTitle().getTextAttr().setAlignText(Style.VERTICALTEXT);
                }
            } else if (axis instanceof CategoryAxis) {
                if (axis.getTitle() != null) {
                    axis.getTitle().getTextAttr().setAlignText(Style.VERTICALTEXT);
                }
            }
        }
    }
}