package com.fr.bi.stable.data.db;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.data.AbstractDataModel;
import com.fr.general.DateUtils;
import com.fr.general.data.TableDataException;

import java.util.List;

/**
 * Created by sheldon on 14-8-8.
 */
public class BIExcelDataModel extends AbstractDataModel {

    private final static int EXCEL_TYPE_XLS = 1;
    private final static int EXCEL_TYPE_XLSX = 2;
    private final static int EXCEL_TYPE_CSV = 3;

    //列名
    private String[] columnNames;
    //表数据
    private List<Object[]> rowDataList;
    //字段类型
    private int[] columnTypes;

    private  String filePath;

    private boolean isDataInit = false;

    public BIExcelDataModel( String filePath, String[] columnNames, int[] columnTypes ) {
        this.filePath = filePath;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    public BIExcelDataModel( String filePath ) {
        this.filePath = filePath;
    }

    @Override
    public int getColumnCount() throws TableDataException {
        if(this.columnNames == null) {
            initData();
        }
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) throws TableDataException {
        return this.columnNames[columnIndex];
    }

    public int getColumnType( int columnIndex )  throws TableDataException  {
        return this.columnTypes[columnIndex];
    }

    @Override
    public int getRowCount() throws TableDataException {
        if(this.rowDataList == null) {
            initData();
        }
        return this.rowDataList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) throws TableDataException {
        initData();
        if( columnIndex > columnNames.length || rowIndex > rowDataList.size() ) {
            return null;
        }
        return rowDataList.get(rowIndex)[columnIndex];
    }

    public Object getValueAt4Preview(int rowIndex, int columnIndex) throws Exception {
        if(columnIndex > columnNames.length || rowIndex > rowDataList.size()){
            return null;
        }
        return rowDataList.get(rowIndex)[columnIndex];
    }

    public String [] onlyGetColumnNames() throws TableDataException {
        if(this.columnNames == null) {
            initPartData();
        }
        return columnNames;
    }

    public int [] onlyGetColumnTypes()  throws TableDataException  {
        if(this.columnTypes == null) {
            initPartData();
        }
        return columnTypes;
    }

    public List<Object[]> getDataList(){
        if(this.rowDataList == null) {
            initData();
        }
        return  rowDataList;
    }

    /**
     * 释放
     * @throws Exception
     */
    @Override
    public void release() throws Exception {

    }

    /**
     * 初始化，全部数据
     */
    private void initData(){
        if (isDataInit) {
            return;
        }
        switch (getExcelType()){
            case EXCEL_TYPE_CSV:
                initExcel4CSV(false);
                break;
            case EXCEL_TYPE_XLS:
                initExcel4XLS(false);
                break;
            case EXCEL_TYPE_XLSX:
                initExcel4XLSX(false);
                break;
        }
        isDataInit = true;
    }

    /**
     * 初始化，获取部分
     */
    private void initPartData(){
        if (this.rowDataList != null){
            return;
        }
        switch (getExcelType()){
            case EXCEL_TYPE_CSV:
                initExcel4CSV(true);
                break;
            case EXCEL_TYPE_XLS:
                initExcel4XLS(true);
                break;
            case EXCEL_TYPE_XLSX:
                initExcel4XLSX(true);
                break;
        }
    }

    private int getExcelType(){
        if(this.filePath.endsWith(".xls")){
            return EXCEL_TYPE_XLS;
        }
        if(this.filePath.endsWith(".xlsx")){
            return EXCEL_TYPE_XLSX;
        }
        return EXCEL_TYPE_CSV;
    }

    /**
     * 初始化07excel
     */
    private void initExcel4XLSX(boolean isPreview){
        long start = System.currentTimeMillis();
        try {
            Excel2007Util excel2007Util = new Excel2007Util(this.filePath, isPreview);
            BILogger.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excel2007Util.getRowDataList();
            columnNames = excel2007Util.getColumnNames();
            columnTypes = excel2007Util.getColumnTypes();
        } catch (Exception e){
            BILogger.getLogger().error(e.getMessage());
        }
    }

    /**
     * 初始化03excel
     */
    private void initExcel4XLS(boolean isPreview){
        long start = System.currentTimeMillis();
        try {
            Excel2003Util excel2003Util = new Excel2003Util(this.filePath, isPreview);
            BILogger.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excel2003Util.getRowDataList();
            columnNames = excel2003Util.getColumnNames();
            columnTypes = excel2003Util.getColumnTypes();
        }catch (Exception e){
            BILogger.getLogger().error(e.getMessage());
        }
    }

    private void initExcel4CSV(boolean isPreview) {
        long start = System.currentTimeMillis();
        try {
            ExcelCSVUtil excelCSVUtil = new ExcelCSVUtil(this.filePath, isPreview);
            BILogger.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excelCSVUtil.getRowDataList();
            columnNames = excelCSVUtil.getColumnNames();
            columnTypes = excelCSVUtil.getColumnTypes();
        }catch (Exception e){
            BILogger.getLogger().error(e.getMessage());
        }
    }
}
