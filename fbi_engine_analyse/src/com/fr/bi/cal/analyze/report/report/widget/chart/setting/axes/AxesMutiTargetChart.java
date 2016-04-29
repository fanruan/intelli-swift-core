package com.fr.bi.cal.analyze.report.report.widget.chart.setting.axes;

import com.fr.base.FRContext;
import com.fr.base.chart.BaseChartCollection;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIAbstractChartSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.BIMutileAxisChartStyle;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.conf.report.style.TargetWarnLineCondition;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.chart.base.ChartAxisPosition;
import com.fr.chart.chartattr.*;
import com.fr.chart.chartdata.ChartSummaryColumn;
import com.fr.chart.chartdata.MoreNameCDDefinition;
import com.fr.chart.chartdata.TopDefinition;
import com.fr.chart.chartglyph.ConditionCollection;
import com.fr.chart.chartglyph.CustomAttr;
import com.fr.chart.charttypes.CustomIndependentChart;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class AxesMutiTargetChart extends BIAbstractChartSetting {
    private String[] left_target = new String[]{};
    private String[] right_target = new String[]{};
    private String dimension;


    /**
     *  left_target:["总回款额", "总支出", "利润"],
     *  right_target:["利润","销售额"],
     *  dimension:"分类轴维度"
     */

    /**
     * 创建CustomPlot的默认系列组合 条件属性
     * seriesIndex == 0 ? ChartConstants.AXIS_LEFT : ChartConstants.AXIS_RIGHT
     */
    private static void createSeriesMap4CustomPlot(BITarget[] left_targets, BITarget[] right_targets, Plot plot) {
        ConditionCollection collection = plot.getConditionCollection();
        collection.clearConditionAttr();
        for (int i = 0, len = left_targets.length; i < len; i++) {
            CustomAttr attr = ChartFactory.createCustomAttrWithType(BIExcutorConstant.CHART.getCombChartType(left_targets[i].getChartType()), ChartAxisPosition.AXIS_RIGHT,i, true);
            collection.addConditionAttr(attr);
        }
        for (int i = 0, len = right_targets.length; i < len; i++) {
            CustomAttr attr = ChartFactory.createCustomAttrWithType(BIExcutorConstant.CHART.getCombChartType(right_targets[i].getChartType()), ChartAxisPosition.AXIS_LEFT, i + left_targets.length, false);
            collection.addConditionAttr(attr);
        }
    }

    private static TopDefinition crateChartMoreValueDefinition(BITarget[] left_targets,
                                                               BITarget[] right_targets,
                                                               String dimension) {
        MoreNameCDDefinition tableDataDef = new MoreNameCDDefinition();
        int left_len = left_targets.length;
        int right_len = right_targets.length;
        if (left_len + right_len > 0) {
            ChartSummaryColumn[] series = new ChartSummaryColumn[left_len + right_len];

            for (int i = 0; i < left_len; i++) {
                series[i] = new ChartSummaryColumn(
                        left_targets[i].getValue(),
                        left_targets[i].getValue(),
                        BIExcutorConstant.CHART.getChartDataFunction());
            }
            for (int i = 0; i < right_len; i++) {
                series[i + left_len] = new ChartSummaryColumn(
                        right_targets[i].getValue(),
                        right_targets[i].getValue(),
                        BIExcutorConstant.CHART.getChartDataFunction());
            }
            tableDataDef.setChartSummaryColumn(series);
        } else {
            tableDataDef.setChartSummaryColumn(new ChartSummaryColumn[]{});
        }

        if (dimension != null) {
            tableDataDef.setCategoryName(dimension);
        }

        return tableDataDef;
    }

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
        if (groups_of_targets.length > 0) {
            left_target = groups_of_targets[0];
        }
        if (groups_of_targets.length > 1) {
            right_target = groups_of_targets[1];
        }
        if (jo.has("style")) {
            style = new BIMutileAxisChartStyle();
            style.parseJSON(jo.getJSONObject("style"));
        }
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
        try {
            BITarget[] left_targets = new BISummaryTarget[left_target.length];
            for (int i = 0; i < left_target.length; i++) {
                left_targets[i] = BITravalUtils.getTargetByName(this.left_target[i], targets);
            }
            BITarget[] right_targets = new BISummaryTarget[right_target.length];
            for (int i = 0; i < right_target.length; i++) {
                right_targets[i] = BITravalUtils.getTargetByName(this.right_target[i], targets);
            }

            Chart[] c = CustomIndependentChart.combChartTypes;
            Chart chart = (Chart) c[0].clone();
            chart.setTitle(null);
            createSeriesMap4CustomPlot(left_targets, right_targets, chart.getPlot());
            chart.getPlot().setHotHyperLink(BIExcutorConstant.CHART.createChartHyperLink(this.dimension, null, widgetName));
            chart.getPlot().setSeriesDragEnable(false);
            chart.getPlot().setPlotStyle(FBIConfig.getInstance().getChartStyleAttr().getDefaultStyle());

            dealWithChartStyle(chart.getPlot());
            ChartCollection cc = new ChartCollection(chart);
            TopDefinition tdf = crateChartMoreValueDefinition(left_targets, right_targets, this.dimension);
            chart.setFilterDefinition(tdf);
            return cc;
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected HashSet<String> getDimensionNameSet() {
        HashSet<String> set = new HashSet<String>();
        if (dimension != null){
            set.add(dimension);
        }
        return set;
    }

    @Override
    protected HashSet<String> getTargetNameSet() {
        HashSet<String> set = new HashSet<String>();
        for (String str : left_target){
            set.add(str);
        }
        for (String str : right_target){
            set.add(str);
        }
        return set;
    }

    @Override
    protected void afterNodeConstructed(BIDimension[] rows, BISummaryTarget[] summary, BISession session, Node node, BaseChartCollection cc) {
        ArrayList<ChartAlertValue> llist = new ArrayList<ChartAlertValue>();
        ArrayList<ChartAlertValue> rlist = new ArrayList<ChartAlertValue>();
        Chart chart = (Chart)cc.getChartWithIndex(0);
        ValueAxis lvalueAxis = (ValueAxis)chart.getPlot().getyAxis();
        ValueAxis rvalueAxis = (ValueAxis)chart.getPlot().getSecondAxis();
        for (int i = 0; i < summary.length; i ++){
            if (summary[i] != null && summary[i].getStyle() != null){
                TargetWarnLineCondition[] warnLineConditions = summary[i].getStyle().getWarnConditions();
                for (int j = 0; j < warnLineConditions.length; j ++){
                    ChartAlertValue alertValue = getAlertValue(session, node, summary[i], warnLineConditions[j]);
                    boolean isLeft = false;
                    for (int k = 0; k < left_target.length; k ++){
                        if (ComparatorUtils.equals(left_target[k], summary[i].getValue())){
                            isLeft = true;
                        }
                    }
                    if (isLeft){
                        llist.add(alertValue);
                    } else {
                        rlist.add(alertValue);
                    }
                }

            }
        }
        if (!llist.isEmpty()){
            ChartAlertValue[] values = new ChartAlertValue[llist.size()];
            lvalueAxis.setAlertValues(llist.toArray(values));
        }
        if (!rlist.isEmpty()){
            ChartAlertValue[] values = new ChartAlertValue[rlist.size()];
            rvalueAxis.setAlertValues(rlist.toArray(values));
        }
    }
}