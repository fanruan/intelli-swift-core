package com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.utils.BITableCellFormatHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.setting.ICellFormatSetting;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/4/10.
 */
public class BITableCellDimFormatOperation implements ITableCellFormatOperation {
    private int typeGroup;
    private ICellFormatSetting ICellFormatSetting;

    public BITableCellDimFormatOperation(int groupType, ICellFormatSetting ICellFormatSetting) {
        this.typeGroup = groupType;
        this.ICellFormatSetting = ICellFormatSetting;
    }

    @Override
    public String formatValues(String text) throws Exception {
        JSONObject format = null != ICellFormatSetting ? ICellFormatSetting.createJSON() : new JSONObject();
        String value = BITableCellFormatHelper.dateFormat(format, typeGroup, text);
        return value;
    }

}
