package com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.setting.ICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.utils.BITableCellFormatHelper;

/**
 * Created by Kary on 2017/4/10.
 */
public class BITableCellTargetFormatOperation implements ITableCellFormatOperation {
    private ICellFormatSetting ICellFormatSetting;

    public BITableCellTargetFormatOperation(ICellFormatSetting ICellFormatSetting) {
        this.ICellFormatSetting = ICellFormatSetting;
    }

    @Override
    public String formatValues(String text) throws Exception {
        return BITableCellFormatHelper.targetValueFormat(ICellFormatSetting.createJSON(),text);
    }

}
