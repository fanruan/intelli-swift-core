package com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.setting.ICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.utils.BITableCellFormatHelper;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/4/10.
 */
public abstract class BITableCellFormatOperation implements ITableCellFormatOperation {
     ICellFormatSetting iCellFormatSetting;

    @Override
    public JSONObject createTextStyle(String text) throws Exception {
        try {
            return BITableCellFormatHelper.createTextStyle(iCellFormatSetting.createJSON(), text);
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return new JSONObject();
    }
}
