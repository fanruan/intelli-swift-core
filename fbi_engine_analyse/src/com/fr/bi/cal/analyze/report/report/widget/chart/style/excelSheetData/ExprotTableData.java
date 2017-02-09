package com.fr.bi.cal.analyze.report.report.widget.chart.style.excelSheetData;

import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartSettingFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.SessionDealWith;

/**
 * Created by Kary on 2017/2/9.
 */
public class ExprotTableData implements ExcelInterface {
    //  提供数据源和数据列
    private BIWidget widget;
    //  真实数据
    private JSONObject data;
    //wb参数
    private JSONObject chartOptions;
    private String sessionID;

    public ExprotTableData(BIWidget widget, JSONObject data, String sessionID) throws Exception {
        this.widget = widget;
        this.data = data;
        chartOptions = parseCHartOptions();
        this.sessionID = sessionID;
    }

    @Override
    public JSONObject parseCHartOptions() throws Exception {
        JSONObject chartOptions = parseChartSetting(sessionID);
        //将plotOptions下的animation设为false否则不能截图（只截到网格线）
        JSONObject plotOptions = (JSONObject) chartOptions.get("plotOptions");
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        return chartOptions;
    }

    public JSONObject convert(String sessionID) throws Exception {
        return new JSONObject().put("data", new JSONArray().put(data));
    }

    public JSONObject getData(String sessionID) throws Exception {
        BISession sessionIDInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        JSONObject dataJSON = widget.createDataJSON(sessionIDInfor);
        return new JSONObject().put("data", dataJSON);
    }

    public JSONObject parseChartSetting(String sessionID) throws Exception {
        return BIChartSettingFactory.parseChartSetting(widget, convert(sessionID).getJSONArray("data"), convert(sessionID).optJSONObject("options"), convert(sessionID).getJSONArray("types"));
    }

    public JSONObject getChartOptions() {
        return chartOptions;
    }
}
