package com.fr.bi.cal.analyze.report.report.widget.chart.setting.tiao;

import com.fr.base.FRContext;
import com.fr.base.chart.BaseChartCollection;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.report.data.widget.chart.BICharDefineFactory;
import com.fr.bi.cal.analyze.report.data.widget.chart.BIChartDefine;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIAbstractChartSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.BISingleAxisChartStyle;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.conf.report.style.TargetWarnLineCondition;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.ChartAlertValue;
import com.fr.chart.chartattr.ChartCollection;
import com.fr.chart.chartattr.ValueAxis;
import com.fr.chart.chartdata.OneValueCDDefinition;
import com.fr.chart.chartdata.TopDefinition;
import com.fr.chart.charttypes.BarIndependentChart;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.HashSet;

public class TiaoOneTargetChart extends BIAbstractChartSetting {

    protected String left_dimension;
    protected String right_dimension;
    protected String target;

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
            left_dimension = groups_of_dimensions[0][0];
        }
        if (groups_of_dimensions.length > 1 && groups_of_dimensions[1].length > 0) {
            right_dimension = groups_of_dimensions[1][0];
        }
        if (groups_of_targets.length > 0 && groups_of_targets[0].length > 0) {
            target = groups_of_targets[0][0];
        }
        if (jo.has("style")) {
            style = new BISingleAxisChartStyle();
            style.parseJSON(jo.getJSONObject("style"));
        }
    }

    protected int getChartType(BISummaryTarget target) {
        return BIExcutorConstant.CHART.TIAO;
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
        BISummaryTarget target = null;
        if (this.target != null) {
            target = (BISummaryTarget) BITravalUtils.getTargetByName(this.target, targets);
        }

        BIChartDefine chartDefine = BICharDefineFactory.getChartDefine(getChartType(target));
        Chart[] c = BarIndependentChart.barChartTypes;

        Chart chart;
        try {
            chart = (Chart) c[chartDefine.getChartType()].clone();
            chart.setTitle(null);
            chart.getPlot().setHotHyperLink(BIExcutorConstant.CHART.createChartHyperLink(this.left_dimension,
                    this.right_dimension, widgetName));
            chart.getPlot().setSeriesDragEnable(false);

            chart.getPlot().setPlotStyle(FBIConfig.getInstance().getChartStyleAttr().getDefaultStyle());

            dealWithChartStyle(chart.getPlot());
            ChartCollection cc = new ChartCollection(chart);
            TopDefinition tdf = crateChartOneValueDefinition(target, this.left_dimension, this.right_dimension);
            chart.setFilterDefinition(tdf);
            return cc;
        } catch (CloneNotSupportedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    private TopDefinition crateChartOneValueDefinition(BISummaryTarget summary,
                                                       String row,
                                                       String column) {
        OneValueCDDefinition tableDataDef = new OneValueCDDefinition();
        tableDataDef.setValueColumnName(summary == null ? StringUtils.EMPTY : summary.getValue());
        tableDataDef.setDataFunction(BIExcutorConstant.CHART.getChartDataFunction());
        setRowColumn(tableDataDef, row, column);
        return tableDataDef;
    }

    protected void setRowColumn(OneValueCDDefinition tableDataDef, String row, String column) {
        if (row != null) {
            tableDataDef.setCategoryName(row);
        }
        if (column != null) {
            tableDataDef.setSeriesColumnName(column);
        }
    }

    @Override
    protected void afterNodeConstructed(BIDimension[] rows, BISummaryTarget[] summary, BISession session, Node node, BaseChartCollection cc) {
        if (summary.length > 0 && summary[0].getStyle() != null){
            Chart chart = (Chart)cc.getChartWithIndex(0);
            TargetWarnLineCondition[] warnLineConditions = summary[0].getStyle().getWarnConditions();
            ValueAxis valueAxis = (ValueAxis)chart.getPlot().getyAxis();
            if( hasAlert()) {
                ChartAlertValue[] alertValues = new ChartAlertValue[warnLineConditions.length];
                for (int i = 0; i < warnLineConditions.length; i ++){
                    alertValues[i] = getAlertValue(session, node, summary[0], warnLineConditions[i]);new ChartAlertValue();
                }
                valueAxis.setAlertValues(alertValues);
            }
        }
    }


    @Override
    protected HashSet<String> getTargetNameSet() {
        HashSet<String> set = new HashSet<String>();
        if (left_dimension != null){
            set.add(left_dimension);
        }
        if (right_dimension != null){
            set.add(right_dimension);
        }
        return set;
    }



    @Override
    protected HashSet<String> getDimensionNameSet() {
        HashSet<String> set = new HashSet<String>();
        if (target != null){
            set.add(target);
        }
        return set;
    }

    protected  boolean hasAlert(){
        return true;
    }
}