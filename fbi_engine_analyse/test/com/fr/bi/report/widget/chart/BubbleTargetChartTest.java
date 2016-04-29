package com.fr.bi.report.widget.chart;

import com.fr.bi.cal.analyze.report.widget.chart.BubbleTargetChart;
import com.fr.bi.cal.analyze.report.widget.chart.style.BIMutileAxisChartStyle;
import com.fr.json.JSONObject;
import junit.framework.TestCase;

/**
 * Created by 小灰灰 on 2014/11/7.
 */
public class BubbleTargetChartTest extends TestCase {
    public void testJSON() {
        BubbleTargetChart chart = new BubbleTargetChart();
        JSONObject jo = new JSONObject();
        try {
            jo.put("style", new JSONObject());
            chart.parseJSON(jo);
        } catch (Exception e) {

        }
        assertEquals(chart.style.getClass(), BIMutileAxisChartStyle.class);
    }
}