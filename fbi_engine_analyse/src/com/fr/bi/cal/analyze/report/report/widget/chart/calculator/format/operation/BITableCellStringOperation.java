package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation;

import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.setting.ICellFormatSetting;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/7/7.
 */
public class BITableCellStringOperation extends BITableCellFormatOperation {
    public BITableCellStringOperation(ICellFormatSetting ICellFormatSetting) {
        this.iCellFormatSetting = ICellFormatSetting;
    }

    @Override
    public String formatItemTextValues(String text) throws Exception {
        return text;
    }

    @Override
    public JSONObject createItemTextStyle(String text) throws Exception {
        return JSONObject.create().put("textAlign", getTextAlign());
    }

    @Override
    public String formatHeaderText(String headerText) throws Exception {
        return headerText;
    }

    protected String getTextAlign() {
        return "left";
    }
}
