package com.fr.bi.cal.analyze.report.report.widget.chart.export.format;

/**
 * Created by Kary on 2017/4/10.
 */
public class TableCellFormatOperation {
    private String dId;
    private int dimType;
    private FormatSetting formatSetting;

    public TableCellFormatOperation(String dId,int dimType, FormatSetting formatSetting) {
        this.dId=dId;
        this.dimType=dimType;
        this.formatSetting = formatSetting;
    }

    public String getdId() {
        return dId;
    }

    public String formatText(String text) {
        return text;
    }
}
