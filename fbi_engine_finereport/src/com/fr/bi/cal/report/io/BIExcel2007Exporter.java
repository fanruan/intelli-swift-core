package com.fr.bi.cal.report.io;

import com.fr.bi.cal.report.io.core.BIExcelExporterBlock;
import com.fr.bi.cal.report.io.iterator.StreamCellCase;
import com.fr.bi.cal.report.io.iterator.TableCellIterator;
import com.fr.bi.cal.report.report.poly.BIPolyAnalyECBlock;
import com.fr.io.exporter.excel.stream.StreamExcel2007Exporter;
import com.fr.io.exporter.poi.wrapper.POIWorkbookAction;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.block.Block;
import com.fr.report.elementcase.ElementCase;
import com.fr.report.poly.AbstractPolyReport;
import com.fr.report.poly.ResultChartBlock;
import com.fr.report.report.ResultReport;
import com.fr.third.v2.org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.List;

public class BIExcel2007Exporter extends StreamExcel2007Exporter {

    private ResultReport[] innerReport;

    public BIExcel2007Exporter(List paperSettingList) {
        this.paperSettingList = paperSettingList;
    }


    @Override
    protected void exportBook(ResultWorkBook book, POIWorkbookAction workbookWrapper, List cellList, List cellFormulaList, List list, boolean reUse) throws Exception {
        int reportCount = book.getReportCount();
        innerReport = new ResultReport[reportCount];
        for (int i = 0; i < reportCount; i++) {
            innerReport[i] = book.getResultReport(i);

            //b:TODO 2 elmentcase
            ElementCase ec = null;
            //b:TODO
            Block block = ((AbstractPolyReport) innerReport[i]).getBlock(0);
            if (block instanceof ResultChartBlock) {
                ec = new BIChartElementCase((ResultChartBlock) block);
            } else {
                ec = (ElementCase) block;
            }
            if (ec instanceof BIPolyAnalyECBlock) {
                StreamCellCase cellCase = (StreamCellCase) ((BIPolyAnalyECBlock) ec).getCellCase();
                int c = 0;
                TableCellIterator tableCellIterator = (TableCellIterator) cellCase.cellIterator();
                while(tableCellIterator.hasNext()) {
                    this.innerExportReport(new BIExcelExporterBlock(innerReport[i], tableCellIterator.next()),
                            book.getReportExportAttr(), book.getReportName(i) + (c == 0 ? "" : "_" + c),
                            (SXSSFWorkbook) workbookWrapper.getWorkbook(), cellList, cellFormulaList, 0);
                    c++;
                }
            }

        }


    }

}