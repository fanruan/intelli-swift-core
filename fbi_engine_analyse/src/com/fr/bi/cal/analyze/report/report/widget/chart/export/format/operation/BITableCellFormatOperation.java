package com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.setting.ICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.utils.BITableCellFormatHelper;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/4/10.
 */
public class BITableCellFormatOperation implements ITableCellFormatOperation {
    private ICellFormatSetting ICellFormatSetting;
    private int typeGroup;

    public BITableCellFormatOperation(ICellFormatSetting ICellFormatSetting) {
        this.ICellFormatSetting = ICellFormatSetting;
    }

    public BITableCellFormatOperation(int groupType, ICellFormatSetting ICellFormatSetting) {
        this.typeGroup = groupType;
        this.ICellFormatSetting = ICellFormatSetting;
    }

    @Override
    public String formatTextValues(String text) throws Exception {
        if (0 != this.typeGroup) {
            JSONObject format = null != ICellFormatSetting ? ICellFormatSetting.createJSON() : new JSONObject();
            return BITableCellFormatHelper.dateFormat(format, typeGroup, text);
        } else {
            return BITableCellFormatHelper.targetValueFormat(ICellFormatSetting.createJSON(), text);
        }

    }

    @Override
    public JSONObject createTextStyle(String text) throws Exception {
        try {
            return BITableCellFormatHelper.createTextStyle(ICellFormatSetting.createJSON(), text);
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return new JSONObject();
    }
}
