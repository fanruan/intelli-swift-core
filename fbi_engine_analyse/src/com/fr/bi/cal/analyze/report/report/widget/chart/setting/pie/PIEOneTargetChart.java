package com.fr.bi.cal.analyze.report.report.widget.chart.setting.pie;

import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.report.data.widget.chart.BICharDefineFactory;
import com.fr.bi.cal.analyze.report.data.widget.chart.BIChartDefine;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIAbstractChartSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.SimpleChartStyle;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.ChartCollection;
import com.fr.chart.chartdata.OneValueCDDefinition;
import com.fr.chart.chartdata.TopDefinition;
import com.fr.chart.charttypes.PieIndependentChart;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.HashSet;

public class PIEOneTargetChart extends BIAbstractChartSetting {
    protected String dimension;
    protected String target;

    /**
     *  dimension:"系列维度",
     *   target:"回款额"
     */

    /**
     * parseJSON方法
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (groups_of_dimensions.length > 0 && groups_of_dimensions[0].length > 0) {
            dimension = groups_of_dimensions[0][0];
        }
        if (groups_of_targets.length > 0 && groups_of_targets[0].length > 0) {
            target = groups_of_targets[0][0];
        }
//
        if (jo.has("style")) {
            style = new SimpleChartStyle();
            style.parseJSON(jo.getJSONObject("style"));
        }
    }

    protected int getChartType() {
        return BIExcutorConstant.CHART.PIE;
    }

    /**
     * 创建图表集合
     *
     * @param dimensions 维度
     * @param targets    目标
     * @param widgetName 控件名
     * @return 图表集合
     */
    @Override
    public ChartCollection createChartCollection(BIDimension[] dimensions,
                                                 BITarget[] targets, String widgetName) {
        BITarget target = null;
        if (this.target != null) {
            target = BITravalUtils.getTargetByName(this.target, targets);
        }
        BIChartDefine chartDefine = BICharDefineFactory.getChartDefine(getChartType());
        Chart[] c = PieIndependentChart.pieChartTypes;

        Chart chart;
        try {
            chart = (Chart) c[chartDefine.getChartType()].clone();
            chart.setTitle(null);
            chart.getPlot().setHotHyperLink(BIExcutorConstant.CHART.createChartHyperLink(null,
                    this.dimension, widgetName));
            chart.getPlot().setSeriesDragEnable(false);
            chart.getPlot().setPlotStyle(FBIConfig.getInstance().getChartStyleAttr().getDefaultStyle());

            dealWithChartStyle(chart.getPlot());
            ChartCollection cc = new ChartCollection(chart);
            TopDefinition tdf = crateChartOneValueDefinition(target, this.dimension);
            chart.setFilterDefinition(tdf);
            return cc;
        } catch (CloneNotSupportedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    protected TopDefinition crateChartOneValueDefinition(BITarget summary,
                                                         String column) {
        OneValueCDDefinition tableDataDef = new OneValueCDDefinition();
        tableDataDef.setValueColumnName(summary == null ? StringUtils.EMPTY : summary.getValue());
        tableDataDef.setDataFunction(BIExcutorConstant.CHART.getChartDataFunction());

        if (column != null) {
            tableDataDef.setSeriesColumnName(column);
        }

        return tableDataDef;
    }

    @Override
    protected HashSet<String> getTargetNameSet() {
        HashSet<String> set = new HashSet<String>();
        set.add(target);
        return set;
    }


    @Override
    protected HashSet<String> getDimensionNameSet() {
        HashSet<String> set = new HashSet<String>();
        if (dimension != null){
            set.add(dimension);
        }
        return set;
    }
}