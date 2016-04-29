package com.fr.bi.cal.report.io;

import com.fr.bi.cal.report.io.core.BIExcelExporterBlock;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.io.exporter.AppExporter;
import com.fr.io.exporter.ExcelExporter;
import com.fr.io.exporter.POIWrapper.POIWorkbookAction;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.block.Block;
import com.fr.report.elementcase.ElementCase;
import com.fr.report.poly.AbstractPolyReport;
import com.fr.report.poly.ResultChartBlock;
import com.fr.report.report.Report;
import com.fr.report.report.ResultReport;
import com.fr.script.Calculator;
import com.fr.stable.ExportConstants;
import com.fr.third.org.apache.poi.hssf.usermodel.HSSFSheet;
import com.fr.third.org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.awt.*;
import java.util.List;

/**
 * @author:ben Administrator
 * @time: 2012 2012-9-5
 * @description:结构上来说和excelexporter同级，需要重构this && excelexporter
 */
public class BIExcelExporter extends ExcelExporter {
    private ResultReport innerReport = null;

    public BIExcelExporter(List paperSettingList) {
        this.paperSettingList = paperSettingList;
    }

    @Override
    protected AppExporter getExporterFor2007(List paperSettingList) throws ClassNotFoundException {
        return new BIExcel2007Exporter(paperSettingList);
    }

    // b:TODO biresultworkbook one sheet one block
    @Override
    protected void exportBook(ResultWorkBook book, POIWorkbookAction workbookWrapper, List cellList,
                              List<String> cellFormulaList, List<Report> reportList, boolean reUse) throws Exception {
        innerReport = book.getResultReport(0);

        Point offset = new Point(0, 0);
        //b:TODO 2 elmentcase
        ElementCase ec = null;
        //b:TODO
        Block block = ((AbstractPolyReport) innerReport).getBlock(0);
        if (block instanceof ResultChartBlock) {
            ec = new BIChartElementCase((ResultChartBlock) block);
        } else {
            ec = (ElementCase) block;
        }

        int columnCount = ec.getColumnCount();
        int rowCount = ec.getRowCount();
        // marks:当报表行数超过最大行数，超过最大列数，自动新建一个excelsheet
        int c = 1;
        while (offset.x < columnCount) {
            offset.y = 0;
            while (offset.y < rowCount) {
                this.innerExportReport(new BIExcelExporterBlock(innerReport, offset.x, offset.y, Math.min(columnCount - offset.x, ExportConstants.MAX_COLS), Math.min(rowCount - offset.y, ExportConstants.MAX_ROWS)),
                        book.getReportExportAttr(), book.getReportName(0) + (c == 1 ? "" : "_" + c),
                        (HSSFWorkbook) workbookWrapper.getWorkbook(), cellList, cellFormulaList, 0);
                offset.y += ExportConstants.MAX_ROWS;
                c++;
            }
            offset.x += ExportConstants.MAX_COLS;
        }
    }

    protected void setAttr4ECReportSIL(Calculator cal, IntList hiddenRowList, IntList hiddenColList, ElementCase report) {
        //b：do nothing
    }

    @Override
    protected void dealECReportAndBlockPageSetting(ElementCase report, HSSFSheet hssfSheet, int reportIndex) {
        dealWithPageSetting(innerReport, hssfSheet, reportIndex);
    }
}