package com.fr.bi.cal.analyze.report.report.widget.chart.style;


import com.fr.base.Style;
import com.fr.bi.conf.report.style.TargetFormat;
import com.fr.bi.conf.report.style.TargetStyleConstant;
import com.fr.bi.conf.report.style.TargetTitle;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.chart.base.*;
import com.fr.chart.chartattr.*;
import com.fr.chart.chartglyph.ConditionAttr;
import com.fr.chart.chartglyph.ConditionCollection;
import com.fr.chart.chartglyph.CustomAttr;
import com.fr.chart.chartglyph.DataSheet;
import com.fr.general.FRFont;
import com.fr.general.Inter;
import com.fr.json.JSONObject;
import com.fr.stable.Constants;

import java.awt.*;

public class BIMutileAxisChartStyle extends AbstractChartStyle {
    private int left_unit = BIExcutorConstant.CHARTSTYLE.VALUE_UNIT_NONE;
    private int right_unit = BIExcutorConstant.CHARTSTYLE.VALUE_UNIT_NONE;
    private TargetFormat left_format = new TargetFormat();
    private TargetFormat right_format = new TargetFormat();
    private TargetTitle left_title = new TargetTitle();
    private TargetTitle right_title = new TargetTitle();

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("left_unit")) {
            this.left_unit = jo.optInt("left_unit", 0);
        }
        if (jo.has("right_unit")) {
            this.right_unit = jo.optInt("right_unit", 0);
        }
        if (jo.has("left_format")) {
            left_format = new TargetFormat();
            left_format.parseJSON(jo.getJSONObject("left_format"));
        }
        if (jo.has("right_format")) {
            right_format = new TargetFormat();
            right_format.parseJSON(jo.getJSONObject("right_format"));
        }
        if (jo.has("left_title")) {
            left_title.parseJSON(jo.getJSONObject("left_title"));
        }
        if (jo.has("right_title")) {
            right_title.parseJSON(jo.getJSONObject("right_title"));
        }
    }

    private void dealWithPointLabel(Plot plot) {
        if (this.showDataPointLabel) {
            ConditionCollection collection = plot.getConditionCollection();
            int size = collection.getConditionAttrSize();
            for (int i = 0; i < size; i++) {
                AttrContents attrContents = new AttrContents();
                CustomAttr attr = (CustomAttr) collection.getConditionAttr(i);
                DataSeriesCondition c = attr.getExisted(AttrAxisPosition.class);
                if (c instanceof AttrAxisPosition) {
                    ChartAxisPosition p = ((AttrAxisPosition) c).getAxisPosition();
                    if (p == ChartAxisPosition.AXIS_LEFT) {
                        attrContents.setFormat(TargetStyleConstant.getFormat(left_format.createFormatString()));
                    } else {
                        attrContents.setFormat(TargetStyleConstant.getFormat(right_format.createFormatString()));
                    }
                }
                FRFont font = FRFont.getInstance("Arial", Font.PLAIN, 8);
                attrContents.setTextAttr(new TextAttr(font));
                attrContents.setSeriesLabel(ChartConstants.VALUE_PARA);
                attrContents.setPosition(plot instanceof PiePlot ? Constants.INSIDE : Constants.OUTSIDE);
                attr.addDataSeriesCondition(attrContents);
            }
            ConditionAttr attr2 = plot.getConditionCollection().getDefaultAttr();
            attr2.removeAll();
            AttrContents attrContents2 = new AttrContents();
            FRFont font = FRFont.getInstance("Arial", Font.PLAIN, 8);
            attrContents2.setFormat(TargetStyleConstant.getFormat(left_format.createFormatString()));
            attrContents2.setTextAttr(new TextAttr(font));
            attrContents2.setSeriesLabel(ChartConstants.VALUE_PARA);
            attrContents2.setPosition(plot instanceof PiePlot ? Constants.INSIDE : Constants.OUTSIDE);
            attr2.addDataSeriesCondition(attrContents2);
        }
    }

    private void dealWithLeftNumberxis(NumberAxis axis, Plot plot) {
        NumberAxis na = axis;
        axis.setFormat(TargetStyleConstant.getFormat(left_format.createFormatString()));
        axis.setTitle(left_title.createTitle());
        if (axis.getTitle() != null) {
            axis.getTitle().getTextAttr().setAlignText(Style.VERTICALTEXT);
            axis.getTitle().getTextAttr().setFRFont(FRFont.getInstance(Inter.getLocText("FR-Designer-All_MSBold"), Font.PLAIN, 9));
        }
        axis.setMainGridStyle(show_grid ? Constants.LINE_THIN : Constants.LINE_NONE);
        switch (left_unit) {
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

    private void dealWithRightNumberAxis(NumberAxis axis, Plot plot, boolean isBubble) {
        NumberAxis na = (NumberAxis) axis;
        axis.setFormat(TargetStyleConstant.getFormat(right_format.createFormatString()));
        //sheldon 双值轴的右值轴默认不显示
        axis.setMainGridStyle(isBubble ? (show_grid ? Constants.LINE_THIN : Constants.LINE_NONE) : Constants.LINE_NONE);
        axis.setTitle(right_title.createTitle());
        if (axis.getTitle() != null) {
            axis.getTitle().getTextAttr().setAlignText(isBubble ? Style.HORIZONTALTEXT : Style.VERTICALTEXT);
            axis.getTitle().getTextAttr().setFRFont(FRFont.getInstance(Inter.getLocText("FR-Designer-All_MSBold"), Font.PLAIN, 9));
        }
        switch (right_unit) {
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
        dealWithPointLabel(plot);
        if (plot != null && plot.getHotTooltipStyle() != null) {
            plot.getHotTooltipStyle().setFormat(TargetStyleConstant.getFormat(left_format.createFormatString()));
        }
        if (plot != null && plot.getDataSheet() != null) {
            DataSheet dataSheet = plot.getDataSheet();
            dataSheet.setFormat(TargetStyleConstant.getFormat(left_format.createFormatString()));
        }
        if (plot != null && plot.getyAxis() != null) {
            Axis axis = plot.getyAxis();
            if (axis instanceof NumberAxis) {
                dealWithLeftNumberxis((NumberAxis) axis, plot);
            }
            boolean isBubble = plot instanceof BubblePlot || plot instanceof XYScatterPlot;
            axis = isBubble ? plot.getxAxis() : plot.getSecondAxis();
            if (axis instanceof NumberAxis) {
                dealWithRightNumberAxis((NumberAxis) axis, plot, isBubble);
            }
        }
    }
}