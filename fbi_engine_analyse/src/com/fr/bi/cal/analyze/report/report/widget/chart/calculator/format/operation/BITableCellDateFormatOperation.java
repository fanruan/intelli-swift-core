package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation;

import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.setting.ICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.utils.BITableCellFormatHelper;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;

/**
 * Created by Kary on 2017/4/10.
 */
public class BITableCellDateFormatOperation extends BITableCellFormatOperation {
    private int typeGroup;

    public BITableCellDateFormatOperation(int groupType, ICellFormatSetting ICellFormatSetting) {
        this.typeGroup = groupType;
        iCellFormatSetting = ICellFormatSetting;
    }

    @Override
    public String formatItemTextValues(String text) throws Exception {
        JSONObject format = null != iCellFormatSetting ? iCellFormatSetting.createJSON() : new JSONObject();
        return BITableCellFormatHelper.dateFormat(format, typeGroup, text);
    }

    @Override
    public String formatHeaderText(String headerText) throws Exception {
        JSONObject format = null != iCellFormatSetting ? iCellFormatSetting.createJSON() : new JSONObject();
        if (StringUtils.isNotEmpty(headerText) && StableUtils.isNumber(headerText)) {
            return BITableCellFormatHelper.dateFormat(format, typeGroup, headerText);
        } else {
            return headerText;
        }
    }

    @Override
    protected String getTextAlign() {
        return "left";
    }
}
