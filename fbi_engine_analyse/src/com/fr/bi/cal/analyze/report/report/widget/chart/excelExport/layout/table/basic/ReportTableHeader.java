package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.basic;

/**
 * Created by Kary on 2017/2/13.
 */
public class ReportTableHeader {
    private String text;
    private String title;
    private String id;

    public ReportTableHeader(String text, String title, String tag) {
        this.text = text;
        this.title = title;
        this.id = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
