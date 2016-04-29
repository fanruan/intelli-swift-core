package com.fr.bi.cal.analyze.report.report.widget.chart;


import com.fr.base.Formula;
import com.fr.base.TableData;
import com.fr.base.chart.BaseChartCollection;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.utils.CubeReadingUtils;
import com.fr.bi.cal.analyze.report.report.widget.ChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.ChartStyle;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.style.TargetWarnLineCondition;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.utils.BITravalUtils;

import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.chart.base.AttrColor;
import com.fr.chart.chartattr.ChartAlertValue;
import com.fr.chart.chartattr.Plot;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.HashSet;

public abstract class BIAbstractChartSetting implements BIChartSetting {
    public ChartStyle style;

    protected String[][] groups_of_dimensions = new String[][]{};

    protected String[][] groups_of_targets = new String[][]{};

    protected String widgetName;

    @Override
    public String getWidgetName() {
        return this.widgetName;
    }

    @Override
    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }

    /**
     * 转成json
     *
     * @param jo jsonobject对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("groups_of_dimensions")) {
            JSONArray ja = jo.getJSONArray("groups_of_dimensions");
            groups_of_dimensions = BIJsonUtils.jsonArrayToStringArray(ja);
        }
        if (jo.has("groups_of_targets")) {
            JSONArray ja = jo.getJSONArray("groups_of_targets");
            groups_of_targets = BIJsonUtils.jsonArrayToStringArray(ja);
        }
    }


    protected void dealWithChartStyle(Plot p) {
        if (style != null) {
            style.dealWithStyle(p);
        }
    }

    @Override
	public final BISummaryTarget[] getUsedTargets(BISummaryTarget[] targets) {
        return BITravalUtils.getTargetsOrDimensionsByName(BISummaryTarget.class, getTargetNameSet(), targets);
    }

    protected abstract HashSet<String>  getTargetNameSet();


    @Override
	public final BIDimension[] getUsedDimensions(BIDimension[] dimensions) {
        return BITravalUtils.getTargetsOrDimensionsByName(BIDimension.class, getDimensionNameSet(), dimensions);
    }


    protected abstract HashSet<String>  getDimensionNameSet();

    /**
     * 获取cube节点
     * @param dimensions 维度
     * @param targets 指标
     * @param session session会话
     * @return node节点
     */
    @Override
    public Node getCubeNode(ChartWidget widget, BIDimension[] dimensions,BISummaryTarget[] targets, BISession session) {
        BISummaryTarget[] summary = getUsedTargets(targets);
        BIDimension[] rows = getUsedDimensions(dimensions);
        return CubeIndexLoader.getInstance(session.getUserId()).loadGroup(widget, summary, rows, dimensions, targets, -1, widget.useRealData(),  session);
    }


    /**
     * 创建tabledata
     *
     * @param widget
     * @param dimensions 维度
     * @param targets    指标
     * @param session    会话
     * @param cc         集合
     * @return tabledata对象
     */
    @Override
    public TableData createTableData(ChartWidget widget, BIDimension[] dimensions, BISummaryTarget[] targets, BISession session, BaseChartCollection cc) {
        BISummaryTarget[] summary = getUsedTargets(targets);
        BIDimension[] rows = getUsedDimensions(dimensions);
        Node node = getCubeNode(widget, dimensions, targets, session);
        afterNodeConstructed(dimensions, targets, session, node, cc);
        return CubeReadingUtils.createChartTableData(node, rows, summary);
    }

    protected void afterNodeConstructed(BIDimension[] rows, BISummaryTarget[] summary, BISession session, Node node, BaseChartCollection cc){

    }

    protected ChartAlertValue getAlertValue(BISession session, Node node, BISummaryTarget target, TargetWarnLineCondition warnLineConditions) {
        ChartAlertValue alertValues = new ChartAlertValue();
        alertValues.setLineColor(new AttrColor(warnLineConditions.getColor()));
        alertValues.setAlertContent(warnLineConditions.getName());
        if (warnLineConditions.getType() == BIExcutorConstant.CHART.WARINGLINE.CUSTOM) {
            alertValues.setAlertValueFormula(new Formula(String.valueOf(warnLineConditions.getDefineNumber())));
        } else if (warnLineConditions.getType() == BIExcutorConstant.CHART.WARINGLINE.AVG) {
            Number v = node.getChildAVGValue(new TargetGettingKey(target.createSummaryKey(session.getLoader()), target.getValue()));
            alertValues.setAlertValueFormula(new Formula(String.valueOf(BIBaseConstant.DEFAULTNUMBERFORMAT.format(v))));
        } else {
            Number v = node.getSummaryValue(new TargetGettingKey(target.createSummaryKey(session.getLoader()), target.getValue()));
            v = v == null ? 0 : v;
            alertValues.setAlertValueFormula(new Formula(String.valueOf(BIBaseConstant.DEFAULTNUMBERFORMAT.format(v))));
        }
        return alertValues;
    }
}