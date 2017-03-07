package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.BICellStyle;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/26.
 */
public class BITableItemStyle implements BICellStyle{
    private String background;
    private String color;
    private String fontWeight;

    public BITableItemStyle(String background, String color, String fontWeight) {
        this.background = background;
        this.color = color;
        this.fontWeight = fontWeight;
    }

    public BITableItemStyle() {
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return new JSONObject();
    }
}
