package com.fr.bi.report.widget.chart;

import com.fr.bi.cal.analyze.report.widget.chart.XYSCATTERTargetChart;
import com.fr.bi.cal.analyze.report.widget.chart.style.BIMutileAxisChartStyle;
import com.fr.json.JSONObject;
import junit.framework.TestCase;

/**
 * Created by 小灰灰 on 2014/11/7.
 */
public class XYSCATTERTargetChartTest extends TestCase {
    public void testJSON() {
        XYSCATTERTargetChart chart = new XYSCATTERTargetChart();
        JSONObject jo = new JSONObject();
        try {
            jo.put("style", new JSONObject());
            chart.parseJSON(jo);
        } catch (Exception e) {

        }
        assertEquals(chart.style.getClass(), BIMutileAxisChartStyle.class);
    }
}