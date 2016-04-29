package com.fr.bi.cal.report.io.core;

import com.fr.base.DynamicUnitList;
import com.fr.bi.cal.report.io.BIChartElementCase;
import com.fr.data.TableDataSource;
import com.fr.io.core.ExcelCellIterator;
import com.fr.io.core.ExcelFloatIterator;
import com.fr.main.FineBook;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.page.*;
import com.fr.privilege.finegrain.ColumnRowPrivilegeControl;
import com.fr.report.ReportHelper;
import com.fr.report.block.Block;
import com.fr.report.block.ResultBlock;
import com.fr.report.cell.CellElement;
import com.fr.report.cell.FloatElement;
import com.fr.report.cell.ResultCellElement;
import com.fr.report.core.ReportHF;
import com.fr.report.elementcase.ElementCase;
import com.fr.report.elementcase.ResultElementCase;
import com.fr.report.poly.AbstractPolyReport;
import com.fr.report.poly.ResultChartBlock;
import com.fr.report.report.Report;
import com.fr.report.report.ResultReport;
import com.fr.script.Calculator;
import com.fr.stable.ColumnRow;
import com.fr.stable.unit.FU;
import com.fr.stable.unit.UNIT;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Iterator;
import java.util.Map;

/**
 * @author:ben Administrator
 * @time: 2012 2012-9-5
 * @description:此类用于处理bi block exporter，暂时这么写，需要重构( report&&block&&elementcase ==> ecblockreport)
 */
public class BIExcelExporterBlock implements ResultReport, ResultElementCase {
    /**
     *
     */
    private static final long serialVersionUID = 5178446864197075830L;
    //b:TODO poly && resultreport重构，涉及到部分比较多，暂时用恶心的写法
    private ResultReport resultReport = null;
    //b:TODO excelexporterreport属性重复，不extends
    private int row;
    private int column;
    private int width;
    private int height;
    //b:TODO chartresultblock
    private BIChartElementCase ecChart = null;

    public BIExcelExporterBlock(ResultReport resultReport, int column, int row, int width, int height) {
        this.resultReport = resultReport;
        this.row = row;
        this.column = column;
        this.width = width;
        this.height = height;
        dealChartBlock();
    }

    private void dealChartBlock() {
        if (getBlock(0) instanceof ResultChartBlock) {
            ecChart = new BIChartElementCase((ResultChartBlock) getBlock(0));
        }
    }

    private ElementCase getElementCase() {
        return ecChart == null ? (ElementCase) getBlock(0) : ecChart;
    }

    public ResultReport getresultReport() {
        return resultReport;
    }

    @Override
    public ReportHFProvider getFooter(int reportPageType) {
        return resultReport.getFooter(reportPageType);
    }

    @Override
    public ReportHFProvider getHeader(int reportPageType) {
        return resultReport.getHeader(reportPageType);
    }

    @Override
    public ReportSettingsProvider getReportSettings() {
        return resultReport.getReportSettings();
    }

    @Override
    public void setReportSettings(ReportSettingsProvider reportSettings) {
        resultReport.setReportSettings(reportSettings);
    }

    @Override
    public FineBook getBook() {
        return resultReport.getBook();
    }

    @Override
    public ResultWorkBook getResultWorkBook() {
        return resultReport.getResultWorkBook();
    }

    @Override
    public void setResultWorkBook(ResultWorkBook book) {
        resultReport.setResultWorkBook(book);
    }

    @Override
    public void setFooter(int reportPageType, ReportHF reportHF) {
        resultReport.setFooter(reportPageType, reportHF);
    }

    @Override
    public void setHeader(int reportPageType, ReportHF reportHF) {
        resultReport.setHeader(reportPageType, reportHF);
    }

    @Override
    public boolean isElementCaseReport() {
        return true;
    }

    @Override
    public Iterator iteratorOfElementCase() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ResultReport getResultReport() {
        return this;
    }

    @Override
    public Iterator cellIterator() {
        return new ExcelCellIterator(getElementCase(), column, row, width, height);
    }

    @Override
    public int getColumnCount() {
        return width;
    }

    @Override
    public FU getColumnWidth(int col) {
        return getElementCase().getColumnWidth(col + column);
    }

    @Override
    public FU getRowHeight(int r) {
        return getElementCase().getRowHeight(r + row);
    }

    @Override
    public Iterator getRow(int rowIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRowCount() {
        return height;
    }

    @Override
    public Iterator floatIterator() {
        return new ExcelFloatIterator(getElementCase(), column, row, width, height);
    }

    public void addCellElement(CellElement cell) {
        throw new UnsupportedOperationException();
    }

    public void addCellElement(CellElement cell, boolean override) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CellElement getCellElement(int column, int row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getCellValue(int column, int row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator getColumn(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertColumn(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertRow(int rowIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator intersect(int column, int row, int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void merge(int rowFrom, int rowTo, int colFrom, int colTo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAllCellElements() {
        throw new UnsupportedOperationException();
    }

    public boolean removeCellElement(CellElement cell) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CellElement removeCellElement(int column, int row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeColumn(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeRow(int rowIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCellValue(int column, int row, Object newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFloatElement(FloatElement floatElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bringFloatElementForward(FloatElement floatElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bringFloatElementToFront(FloatElement floatElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FloatElement getFloatElement(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAllFloatElements() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeFloatElement(FloatElement floatElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FloatElement removeFloatElement(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendFloatElementBackward(FloatElement floatElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendFloatElementToBack(FloatElement floatElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readXML(XMLableReader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeCommonXML(XMLPrintWriter writer) {
        throw new UnsupportedOperationException();
    }

    public boolean swapCellElement(ColumnRow cr1, ColumnRow cr2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultCellElement getResultCellElement(int column, int row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getColumnMappingArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getRowMappingArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recalculate(Report report, Map parameterMap) {
        throw new UnsupportedOperationException();
    }

    public Report getReport() {
        return this.getResultReport();
    }

    public void setReport(Report report) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setColumnWidth(int column, UNIT newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRowHeight(int row, UNIT newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReportPageAttrProvider getReportPageAttr() {
        return resultReport.getReportPageAttr();
    }

    @Override
    public void setReportPageAttr(ReportPageAttrProvider reportPageAttr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PageSetProvider generateReportPageSet(PaperSettingProvider setting) {
        throw new UnsupportedOperationException();
    }

    public Block getBlock(int index) {
        return ((AbstractPolyReport) resultReport).getBlock(index);
    }

    /**
     * b:TODO 重构后支持，暂时用getblock代替
     *
     * @param index
     * @return resultblock
     */
    public ResultBlock getResultBlock(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TableDataSource getTableDataSource() {
        return null;
    }

    @Override
    public void shrinkTOFitRowHeightForCellElement(CellElement cellElement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void shrinkTOFitColumnWidthForCellElement(CellElement cellElement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DynamicUnitList getRowHeightList_DEC() {
        return ReportHelper.createRowHeightList(this);
    }

    @Override
    public DynamicUnitList getColumnWidthList_DEC() {
        return ReportHelper.createColumnWidthList(this);
    }

    /* (non-Javadoc)
     * @see com.fr.report.elementcase.ElementCase#addRowPrivilegeControl(int, java.lang.String)
     */
    @Override
    public void addRowPrivilegeControl(int row, String selectedRole) {


    }

//	/* (non-Javadoc)
//	 * @see com.fr.report.elementcase.ElementCase#addColumPrivilegeControl(int, java.lang.String)
//	 */
//	@Override
//	public void addColumPrivilegeControl(int column, String selectedRole) {
//
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see com.fr.report.elementcase.ElementCase#removeColumPrivilegeControl(int, java.lang.String)
//	 */
//	@Override
//	public void removeColumPrivilegeControl(int column, String selectedRole) {
//
//		
//	}

    /* (non-Javadoc)
     * @see com.fr.report.elementcase.ElementCase#removeRowPrivilegeControl(int, java.lang.String)
     */
    @Override
    public void removeRowPrivilegeControl(int row, String selectedRole) {


    }

    /* (non-Javadoc)
     * @see com.fr.report.elementcase.ElementCase#getRowPrivilegeControl(int)
     */
    @Override
    public ColumnRowPrivilegeControl getRowPrivilegeControl(int row) {

        return null;
    }

    /* (non-Javadoc)
     * @see com.fr.report.elementcase.ElementCase#getColumnPrivilegeControl(int)
     */
    @Override
    public ColumnRowPrivilegeControl getColumnPrivilegeControl(int column) {

        return null;
    }

    @Override
    public void shrinkToFitPaper(Calculator calculator) {

    }

    @Override
    public void addColumnPrivilegeControl(int arg0, String arg1) {


    }

    @Override
    public void removeColumnPrivilegeControl(int arg0, String arg1) {


    }

//	@Override
//	public List recalculate(Report report, Map parameterMap, boolean reCalRF) {
//
//		return null;
//	}
}