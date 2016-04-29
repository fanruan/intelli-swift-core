package com.fr.bi.cal.analyze.report.report.widget.chart.setting.map;

import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.report.data.widget.chart.BICharDefineFactory;
import com.fr.bi.cal.analyze.report.data.widget.chart.BIChartDefine;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIAbstractChartSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.style.SimpleChartStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.ChartCollection;
import com.fr.chart.chartattr.MapPlot;
import com.fr.chart.chartattr.Plot;
import com.fr.chart.chartdata.MapMoreLayerTableDefinition;
import com.fr.chart.chartdata.MapSingleLayerTableDefinition;
import com.fr.chart.chartdata.SeriesDefinition;
import com.fr.chart.chartdata.TopDefinition;
import com.fr.chart.charttypes.MapIndependentChart;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

import java.util.HashSet;

public class MapChart extends BIAbstractChartSetting {

    private String name_of_map;

    private Layer[] layer = new Layer[0];

    /**
     * 转成json
     *
     * @param jo jsonobject对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("name_of_map")) {
            this.name_of_map = jo.getString("name_of_map");
        }

        if (groups_of_dimensions.length > 0) {
            this.layer = new Layer[groups_of_dimensions[0].length];
            for (int i = 0, len = this.layer.length; i < len; i++) {
                this.layer[i] = new Layer();
                this.layer[i].region_dimension = groups_of_dimensions[0][i];
                if (groups_of_targets.length > 0) {
                    this.layer[i].targets = groups_of_targets[0];
                }
            }
        }
//		if(jo.has("layers")){
//			JSONArray ja = jo.getJSONArray("layers");
//			this.layer = new Layer[ja.length()];
//			for(int i = 0; i < ja.length(); i++){
//				this.layer[i] = new Layer();
//				this.layer[i].parseJSON(ja.getJSONObject(i));
//			}
//		}
        if (jo.has("style")) {
            style = new SimpleChartStyle();
            style.parseJSON(jo.getJSONObject("style"));
        }
    }

    protected int getChartType() {
        return BIExcutorConstant.CHART.MAP;
    }

    /**
     * 创建图表集合
     *
     * @param dimensions 维度
     * @param targets    指标
     * @param widgetName 控件名
     * @return 图表集合
     */
    @Override
    public ChartCollection createChartCollection(BIDimension[] dimensions,
                                                 BITarget[] targets, String widgetName) {
        BIChartDefine chartDefine = BICharDefineFactory.getChartDefine(getChartType());
        Chart[] c = MapIndependentChart.mapChartTypes;

        Chart chart;
        try {
            chart = (Chart) c[chartDefine.getChartType()].clone();
            chart.setTitle(null);
            chart.getPlot().setHotHyperLink(BIExcutorConstant.CHART.createChartMapHyperLink(widgetName));
            chart.getPlot().setSeriesDragEnable(false);

            dealWithChartStyle(chart.getPlot());
            ChartCollection cc = new ChartCollection(chart);
            chart.setFilterDefinition(crateChartMoreValueDefinition());
            return cc;
        } catch (CloneNotSupportedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void dealWithChartStyle(Plot p) {
        super.dealWithChartStyle(p);
        if (p instanceof MapPlot) {
            ((MapPlot) p).setMapName(name_of_map);
        }
    }

    @Override
    protected HashSet<String> getTargetNameSet() {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < layer.length; i++) {
            Layer l = layer[i];
            for (int j = 0; j < l.targets.length; j++) {
                set.add(l.targets[j]);
            }
        }
        return set;
    }

    protected TopDefinition crateChartMoreValueDefinition() {
        TopDefinition define;
        if (layer.length == 1) {
            Layer l = layer[0];
            define = createDefine(l);
        } else {
            MapMoreLayerTableDefinition d = new MapMoreLayerTableDefinition();
            for (int i = 0; i < layer.length; i++) {
                d.addNameValue(createDefine(layer[i]));
            }
            define = d;
        }
        return define;

    }

    private MapSingleLayerTableDefinition createDefine(Layer l) {
        MapSingleLayerTableDefinition d = new MapSingleLayerTableDefinition();
        for (int i = 0; i < l.targets.length; i++) {
            SeriesDefinition def = new SeriesDefinition(l.targets[i], l.targets[i]);
            d.addTitleValue(def);
        }
        d.setAreaName(l.region_dimension == null ? l.targets[0] : l.region_dimension);
        return d;
    }

    @Override
    protected HashSet<String> getDimensionNameSet() {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < layer.length; i++) {
            Layer l = layer[i];
            if (l.region_dimension != null) {
                set.add(l.region_dimension);
            }
        }
        return set;
    }

    private class Layer implements JSONParser {
        private String region_dimension;
        private String[] targets = new String[0];

        @Override
        public void parseJSON(JSONObject jo) throws Exception {
            if (jo.has("region_dimension")) {
                this.region_dimension = jo.getString("region_dimension");
            }
            if (jo.has("targets")) {
                JSONArray ja = jo.getJSONArray("targets");
                targets = new String[ja.length()];
                for (int i = 0; i < ja.length(); i++) {
                    targets[i] = ja.getString(i);
                }
            }
        }

    }


}