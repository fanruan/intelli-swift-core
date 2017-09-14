package com.fr.bi.cal.report.io;

import com.fr.bi.cal.report.io.core.BIExcelExporterBlock;
import com.fr.bi.cal.report.io.iterator.StreamCellCase;
import com.fr.bi.cal.report.io.iterator.StreamPagedIterator;
import com.fr.bi.cal.report.report.poly.BIPolyAnalyECBlock;
import com.fr.io.exporter.excel.stream.StreamExcel2007Exporter;
import com.fr.io.exporter.poi.wrapper.POIWorkbookAction;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.block.Block;
import com.fr.report.poly.AbstractPolyECBlock;
import com.fr.report.poly.AbstractPolyReport;
import com.fr.report.report.ResultReport;
import com.fr.stable.ExportConstants;
import com.fr.third.v2.org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.awt.Point;
import java.util.ArrayList;
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

            int c = 0, rowCount = 0;
            Point offset = new Point(0, 0);
            Block block = ((AbstractPolyReport) innerReport[i]).getBlock(0);

            if (block instanceof BIPolyAnalyECBlock) {
                StreamCellCase streamCellCase = (StreamCellCase) ((BIPolyAnalyECBlock) block).getCellCase();
                StreamPagedIterator tableCellIter = streamCellCase.getTableIterator().getPageIterator();

                ArrayList<StreamPagedIterator> currentPageIter = new ArrayList<StreamPagedIterator>();
                while (tableCellIter.hasNext()) {
                    if ((rowCount & ExportConstants.MAX_ROWS_2007) == 0) {
                        StreamPagedIterator pagedIterator = new StreamPagedIterator();
                        currentPageIter.add(pagedIterator);
                        this.innerExportReport(new BIExcelExporterBlock(innerReport[i], pagedIterator),
                                book.getReportExportAttr(), book.getReportName(i) + (c == 0 ? "" : "_" + c),
                                (SXSSFWorkbook) workbookWrapper.getWorkbook(), cellList, cellFormulaList, 0);
                        offset.y += ExportConstants.MAX_ROWS_2007;
                        if(c > 0) {
                            currentPageIter.get(c - 1).finish();
                        }
                        c++;
                    }
                    rowCount++;
                    currentPageIter.get(c - 1).addCell(tableCellIter.next());
                }
            }


        }


    }

}