package com.fr.bi.cal.analyze.report.report.widget.chart.style.excelSheetData;

import com.fr.bi.cal.analyze.report.report.widget.MultiChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartDataConvertFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartSettingFactory;
import com.fr.bi.conf.report.BIWidget;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/8.
 */
public class ExcelExportPicData implements ExcelInterface {
    //  提供数据源和数据列
    private BIWidget widget;
    //  真实数据
    private JSONObject data;
    //wb参数
    private JSONObject chartOptions;


    public ExcelExportPicData(BIWidget widget, JSONObject data) throws Exception {
        this.widget = widget;
        this.data = data;
        chartOptions = parseCHartOptions();

    }

    @Override
    public JSONObject parseCHartOptions() throws Exception {
        JSONObject chartOptions = parseChartSetting();
        //将plotOptions下的animation设为false否则不能截图（只截到网格线）
        JSONObject plotOptions = (JSONObject) chartOptions.get("plotOptions");
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        return chartOptions;
    }

    public JSONObject convert() {
        return BIChartDataConvertFactory.convert((MultiChartWidget) widget, data);
    }

    public JSONObject parseChartSetting() throws Exception {
        return BIChartSettingFactory.parseChartSetting((MultiChartWidget) widget, convert().getJSONArray("data"), convert().optJSONObject("options"), convert().getJSONArray("types"));
    }

    @Override
    public JSONObject getChartOptions() {
        return chartOptions;
    }
}
