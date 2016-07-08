package com.fr.bi.cal.report.io;

import com.fr.bi.cal.report.io.core.BIExcelExporterBlock;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.io.exporter.excel.stream.StreamExcel2007Exporter;
import com.fr.io.exporter.poi.wrapper.POIWorkbookAction;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.block.Block;
import com.fr.report.elementcase.ElementCase;
import com.fr.report.poly.AbstractPolyReport;
import com.fr.report.poly.ResultChartBlock;
import com.fr.report.report.ResultReport;
import com.fr.script.Calculator;
import com.fr.stable.ExportConstants;
import org.apache.poi.hslf.model.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.awt.*;
import java.util.List;

public class BIExcel2007Exporter extends StreamExcel2007Exporter {

    private ResultReport innerReport = null;

    public BIExcel2007Exporter(List paperSettingList) {
        this.paperSettingList = paperSettingList;
    }


    @Override
    protected void exportBook(ResultWorkBook book, POIWorkbookAction workbookWrapper, List cellList, List cellFormulaList, List list, boolean reUse) throws Exception {
        innerReport = book.getResultReport(0);

        //b:TODO 2 elmentcase
        ElementCase ec = null;
        Point offset = new Point(0, 0);
        //b:TODO
        Block block = ((AbstractPolyReport) innerReport).getBlock(0);
        if (block instanceof ResultChartBlock) {
            ec = new BIChartElementCase((ResultChartBlock) block);
        } else {
            ec = (ElementCase) block;
        }

        int columnCount = ec.getColumnCount();
        int rowCount = ec.getRowCount();
        int c = 1;
        while (offset.x < columnCount) {
            offset.y = 0;
            while (offset.y < rowCount) {
                this.innerExportReport(new BIExcelExporterBlock(innerReport, offset.x, offset.y, Math.min(columnCount - offset.x, ExportConstants.MAX_COLS_2007), Math.min(rowCount - offset.y, ExportConstants.MAX_ROWS_2007)),
                        book.getReportExportAttr(), book.getReportName(0) + (c == 1 ? "" : "_" + c),
                        (SXSSFWorkbook) workbookWrapper.getWorkbook(), cellList, cellFormulaList, 0);
                offset.y += ExportConstants.MAX_ROWS_2007;
                c++;
            }
            offset.x += ExportConstants.MAX_COLS_2007;
        }
    }

}