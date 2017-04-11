package com.fr.bi.cal.analyze.report.report.widget.chart.export.basic;

import com.fr.bi.conf.report.style.ChartSetting;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

/**
 * Created by Kary on 2017/4/10.
 *
 */
public class DimAndTargetStyle implements JSONCreator,JSONParser {
    private String dId;
//    private boolean isUsed;
//    private String text;
//    private int type;
    private ChartSetting chartSetting;
//    private int region;

//    public DimAndTargetStyle(String dId, String text, int type, int region) {
//        this.dId = dId;
//        this.region = region;
//        this.type = type;
//        this.text = text;
//    }

    public DimAndTargetStyle(String dId, ChartSetting chartSetting) {
        this.dId = dId;
        this.chartSetting = chartSetting;
    }

    public ChartSetting getChartSetting() {
        return chartSetting;
    }

    public void setChartSetting(ChartSetting chartSetting) {
        this.chartSetting = chartSetting;
    }

//    public void setRegion(int region) {
//        this.region = region;
//    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("dId", dId);
//        jo.put("chartSetting",chartSetting);
//        jo.put("text", text);
//        jo.put("type", type);
//        jo.put("used", isUsed);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        this.dId=jo.getString("dId");
         this.chartSetting=new ChartSetting();
        chartSetting.parseJSON(jo.getJSONObject("chartSetting"));
    }
}
