package com.fr.bi.cal.analyze.report.report.widget.chart.setting.scatter;

import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIAbstractChartSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.BIMutileAxisChartStyle;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.ChartCollection;
import com.fr.chart.chartattr.Plot;
import com.fr.chart.chartdata.ScatterTableDefinition;
import com.fr.chart.chartdata.TopDefinition;
import com.fr.chart.charttypes.XYScatterIndependentChart;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.HashSet;

/**
 * Created by 小灰灰 on 2014/7/11.
 */
public class XYSCATTERTargetChart extends BIAbstractChartSetting {

    protected String xtarget;
    protected String ytarget;
    protected String dimension;

    private boolean judgeGroupLength() {
        return groups_of_targets.length > 0 && groups_of_targets[0].length > 0 && groups_of_targets[1].length > 0;
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

        if (judgeGroupLength()) {
            xtarget = groups_of_targets[1][0];
            ytarget = groups_of_targets[0][0];
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
    public ChartCollection createChartCollection(BIDimension[] dimensions, BITarget[] targets, String widgetName) {
        try {
            BITarget x_target = BITravalUtils.getTargetByName(xtarget, targets);
            BITarget y_target = BITravalUtils.getTargetByName(ytarget, targets);
            Chart[] c = XYScatterIndependentChart.XYScatterChartTypes;
            Chart chart = (Chart) c[0].clone();
            chart.setTitle(null);
            chart.getPlot().setHotHyperLink(BIExcutorConstant.CHART.createChartHyperLink(this.dimension, null, widgetName));
            chart.getPlot().setSeriesDragEnable(false);
            chart.getPlot().setPlotStyle(FBIConfig.getInstance().getChartStyleAttr().getDefaultStyle());

            dealWithChartStyle(chart.getPlot());
            ChartCollection cc = new ChartCollection(chart);
            TopDefinition tdf = crateChartMoreValueDefinition(x_target, y_target, this.dimension);
            chart.setFilterDefinition(tdf);
            return cc;
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    private TopDefinition crateChartMoreValueDefinition(BITarget xtarget, BITarget ytarget, String dimension) {
        ScatterTableDefinition tableDataDef = new ScatterTableDefinition();
        tableDataDef.setScatterX(xtarget == null ? StringUtils.EMPTY : xtarget.getValue());
        tableDataDef.setScatterY(ytarget == null ? StringUtils.EMPTY : ytarget.getValue());
        tableDataDef.setSeriesName(dimension == null ? StringUtils.EMPTY : dimension);
        return tableDataDef;
    }

    @Override
    protected void dealWithChartStyle(Plot p) {
        if (style != null) {
            style.dealWithStyle(p);
        }
    }

    @Override
    protected HashSet<String> getTargetNameSet() {
        HashSet<String> set = new HashSet<String>();
        if (xtarget != null){
            set.add(xtarget);
        }
        if (ytarget != null){
            set.add(ytarget);
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