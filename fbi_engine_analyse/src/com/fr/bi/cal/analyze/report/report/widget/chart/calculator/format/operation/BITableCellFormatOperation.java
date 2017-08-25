package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.setting.ICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.utils.BITableCellFormatHelper;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/4/10.
 */
public abstract class BITableCellFormatOperation implements ITableCellFormatOperation {
    ICellFormatSetting iCellFormatSetting;

    @Override
    public JSONObject createItemTextStyle(String text) throws Exception {
        JSONObject textStyle = JSONObject.create();
        try {
            textStyle = BITableCellFormatHelper.createTextStyle(iCellFormatSetting.createJSON(), text);
            textStyle.put("textAlign", getTextAlign());
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return textStyle;
    }

    protected abstract String getTextAlign();
}
