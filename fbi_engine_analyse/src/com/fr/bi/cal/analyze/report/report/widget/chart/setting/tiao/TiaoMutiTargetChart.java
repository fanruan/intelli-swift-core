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
import com.fr.chart.chartdata.ChartSummaryColumn;
import com.fr.chart.chartdata.MoreNameCDDefinition;
import com.fr.chart.chartdata.TopDefinition;
import com.fr.chart.charttypes.BarIndependentChart;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class TiaoMutiTargetChart extends BIAbstractChartSetting {
    private String dimension;
    private String[] target = new String[]{};


    /**
     *  dimension:"分类轴维度",
     *    target:["总回款额", "总支出", "利润"]
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
        if (groups_of_targets.length > 0) {
            target = groups_of_targets[0];
        }
//		if(jo.has("classify")){
//			dimension = jo.getString("classify");
//		}
//		if(jo.has("target")){
//			JSONArray ja = jo.getJSONArray("target");
//			target = new String[ja.length()];
//			for(int i = 0, len = ja.length(); i < len; i++){
//				target[i] = ja.getString(i);
//			}
//		}
        if (jo.has("style")) {
            style = new BISingleAxisChartStyle();
            style.parseJSON(jo.getJSONObject("style"));
        }
    }

    protected int getChartType() {
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
        BISummaryTarget[] usedTargets = new BISummaryTarget[target.length];
        for (int i = 0; i < target.length; i++) {
            usedTargets[i] = (BISummaryTarget) BITravalUtils.getTargetByName(this.target[i], targets);
        }
        BIChartDefine chartDefine = BICharDefineFactory.getChartDefine(getChartType());

        Chart[] c = BarIndependentChart.barChartTypes;

        Chart chart;
        try {
            chart = (Chart) c[chartDefine.getChartType()].clone();
            chart.setTitle(null);
            chart.getPlot().setHotHyperLink(BIExcutorConstant.CHART.createChartHyperLink(this.dimension,
                    null, widgetName));
            chart.getPlot().setSeriesDragEnable(false);
            chart.getPlot().setPlotStyle(FBIConfig.getInstance().getChartStyleAttr().getDefaultStyle());

            dealWithChartStyle(chart.getPlot());
            ChartCollection cc = new ChartCollection(chart);
            TopDefinition tdf = crateChartMoreValueDefinition(usedTargets, this.dimension);
            chart.setFilterDefinition(tdf);
            return cc;
        } catch (CloneNotSupportedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    private TopDefinition crateChartMoreValueDefinition(BISummaryTarget[] summarys,
                                                        String row) {
        MoreNameCDDefinition tableDataDef = new MoreNameCDDefinition();
        int colLen = summarys.length;
        if (colLen > 0) {
            ChartSummaryColumn[] series = new ChartSummaryColumn[colLen];
            for (int i = 0; i < colLen; i++) {
                series[i] = new ChartSummaryColumn(
                        summarys[i].getValue(),
                        summarys[i].getValue(),
                        BIExcutorConstant.CHART.getChartDataFunction());
            }
            tableDataDef.setChartSummaryColumn(series);
        } else {
            tableDataDef.setChartSummaryColumn(new ChartSummaryColumn[]{});
        }
        if (row != null) {
            tableDataDef.setCategoryName(row);
        }
        return tableDataDef;
    }



    @Override
    protected void afterNodeConstructed(BIDimension[] rows, BISummaryTarget[] summary, BISession session, Node node, BaseChartCollection cc) {
        ArrayList<ChartAlertValue> list = new ArrayList<ChartAlertValue>();
        Chart chart = (Chart)cc.getChartWithIndex(0);
        ValueAxis valueAxis = (ValueAxis)chart.getPlot().getyAxis();

        if( hasAlert() ) {
            for (int i = 0; i < summary.length; i ++){
                if (summary[i] != null && summary[i].getStyle() != null){
                    TargetWarnLineCondition[] warnLineConditions = summary[i].getStyle().getWarnConditions();
                    for (int j = 0; j < warnLineConditions.length; j ++){
                        ChartAlertValue alertValue  = getAlertValue(session, node, summary[i], warnLineConditions[j]);
                        list.add(alertValue);
                    }
                }
            }
            if (!list.isEmpty()){
                ChartAlertValue[] values = new ChartAlertValue[list.size()];
                valueAxis.setAlertValues(list.toArray(values));
            }
        }
    }

    protected boolean hasAlert(){
        return true;
    }

    @Override
    protected HashSet<String> getTargetNameSet() {
        HashSet<String> set = new HashSet<String>();
        for (String str : target){
            set.add(str);
        }
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