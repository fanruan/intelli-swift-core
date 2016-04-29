package com.fr.bi.cal.analyze.report.report.widget.chart;

import com.fr.base.TableData;
import com.fr.base.chart.BaseChartCollection;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.report.report.widget.ChartWidget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.chart.chartattr.ChartCollection;
import com.fr.json.JSONParser;

public interface BIChartSetting extends JSONParser {

    String getWidgetName();

    void setWidgetName(String widgetName);

    /**
     * 创建图表集合
     *
     * @param dimensions 维度
     * @param targets    指标
     * @param widgetName 控件名
     * @return 集合
     */
    ChartCollection createChartCollection(BIDimension[] dimensions,
                                          BITarget[] targets, String widgetName);

    /**
     * 获取使用的指标
     *
     * @param targets
     * @return 指标
     */
    BISummaryTarget[] getUsedTargets(BISummaryTarget[] targets);

    /**
     * 获取使用的维度
     *
     * @param dimensions
     * @return 维度
     */
    BIDimension[] getUsedDimensions(BIDimension[] dimensions);

    /**
     * 创建tabledata
     *
     * @param dimensions 维度
     * @param targets    指标
     * @param session    会话
     * @param cc         集合
     * @return tabledata对象
     */
    TableData createTableData(ChartWidget widget, BIDimension[] dimensions, BISummaryTarget[] targets, BISession session, BaseChartCollection cc);

    /**
     * 获取节点
     *
     * @param dimensions 维度
     * @param targets    指标
     * @param bind       bind对象
     * @param drill      钻取对象
     * @param session    session会话
     * @return Node节点
     */
    Node getCubeNode(ChartWidget widget, BIDimension[] dimensions, BISummaryTarget[] targets, BISession session);
}