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
import com.fr.chart.chartdata.ChartSummaryColumn;
import com.fr.chart.chartdata.MoreNameCDDefinition;
import com.fr.chart.chartdata.TopDefinition;
import com.fr.chart.charttypes.PieIndependentChart;
import com.fr.json.JSONObject;

import java.util.HashSet;

public class PIEMutiTargetChart extends BIAbstractChartSetting {
    protected String[] target = new String[]{};
    /**
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
        if (groups_of_targets.length > 0) {
            target = groups_of_targets[0];
        }
//		if(jo.has("target")){
//			JSONArray ja = jo.getJSONArray("target");
//			target = new String[ja.length()];
//			for(int i = 0, len = ja.length(); i < len; i++){
//				target[i] = ja.getString(i);
//			}
//		}
        if (jo.has("style")) {
            style = new SimpleChartStyle();
            style.parseJSON(jo.getJSONObject("style"));
        }
    }

    @Override
    protected HashSet<String> getTargetNameSet() {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < target.length; i++) {
            set.add(this.target[i]);
        }
        return set;
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
        BITarget[] usedTargets = new BITarget[target.length];
        for (int i = 0; i < target.length; i++) {
            usedTargets[i] = BITravalUtils.getTargetByName(this.target[i], targets);
        }
        BIChartDefine chartDefine = BICharDefineFactory.getChartDefine(getChartType());
        Chart[] c = PieIndependentChart.pieChartTypes;

        Chart chart;
        try {
            chart = (Chart) c[chartDefine.getChartType()].clone();
            chart.setTitle(null);
            chart.getPlot().setHotHyperLink(BIExcutorConstant.CHART.createChartHyperLink(null,
                    null, widgetName));
            chart.getPlot().setSeriesDragEnable(false);
            chart.getPlot().setPlotStyle(FBIConfig.getInstance().getChartStyleAttr().getDefaultStyle());

            dealWithChartStyle(chart.getPlot());
            ChartCollection cc = new ChartCollection(chart);
            chart.setFilterDefinition(crateChartMoreValueDefinition(usedTargets));
            return cc;
        } catch (CloneNotSupportedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    protected TopDefinition crateChartMoreValueDefinition(BITarget[] summarys) {
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

        return tableDataDef;
    }


    @Override
    protected HashSet<String> getDimensionNameSet() {
        return new HashSet<String>();
    }

}