package com.fr.bi.stable.data.db;

import com.fr.base.FRContext;
import com.fr.base.TableData;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.data.TableDataSource;
import com.fr.general.ComparatorUtils;
import com.fr.general.data.DataModel;
import com.fr.script.Calculator;
import com.fr.stable.ParameterProvider;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sheldon on 14-8-8.
 */
public class BIExcelTableData implements TableData {

    private String filePath = null;

    private boolean needColumnName = true;
    private String[] columnNames;
    private int[] columnTypes;

    public BIExcelTableData(String filePath) {
        this.filePath = filePath;
    }

    public BIExcelTableData(String filePath, String[] columnNames, int[] columnTypes) {
        this.filePath = FRContext.getCurrentEnv().getPath() + BIBaseConstant.EXCELDATA.EXCEL_DATA_PATH + File.separator + filePath;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    /**
     * 创建数据表格
     */
    public BIExcelDataModel createDataModel() {
        return new BIExcelDataModel(filePath,
                columnNames, columnTypes);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 是否需要列名
     *
     * @return 第一行是列名
     */
    public boolean needColumnName() {
        return this.needColumnName;
    }

    public void setNeedColumnName(boolean need) {
        this.needColumnName = need;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tmpTagName = reader.getTagName();
            String tmpVal;
            if ("ExcelTableDataAttr".equals(tmpTagName)) {
                if ((tmpVal = reader.getAttrAsString("filePath", null)) != null) {
                    this.filePath = tmpVal;
                }

                this.needColumnName = !reader.getAttrAsString("needColumnName", "").isEmpty();
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG("ExcelTableDataAttr")
                .attr("needColumnName", this.needColumnName())
                .attr("filePath", this.getFilePath()).end();

    }

    /**
     * 获取数据集中所使用的参数
     *
     * @param calculator 连接上下文计算的算子
     * @return 参数数组
     */
    @Override
    public ParameterProvider[] getParameters(Calculator calculator) {
        return new ParameterProvider[0];
    }

    /**
     * 返回获取数据的执行对象
     * 系统取数时，调用此方法来返回一个获取数据的执行对象
     * 注意！ 当数据集需要根据不同参数来多次取数时，此方法在一个计算过程中会被多次调用。
     *
     * @param calculator 连接上下文计算的算子
     * @return 表示数据集结果的二维表
     */
    @Override
    public DataModel createDataModel(Calculator calculator) {
        return createDataModel();
    }

    /**
     * 返回获取数据的执行对象
     * 系统取数时，调用此方法来返回一个获取数据的执行对象
     * 注意！ 当数据集需要根据不同参数来多次取数时，此方法在一个计算过程中会被多次调用。
     *
     * @param calculator 连接上下文计算的算子
     * @param name       数据集的名字
     * @return 表示数据集结果的二维表
     */
    @Override
    public DataModel createDataModel(Calculator calculator, String name) {
        return createDataModel();
    }

    /**
     * 返回获取数据的执行对象
     * 系统取数时，调用此方法来返回一个获取数据的执行对象
     * 注意！ 当数据集需要根据不同参数来多次取数时，此方法在一个计算过程中会被多次调用。
     *
     * @param calculator 连接上下文计算的算子
     * @param rowCount   要获取数据的行数
     * @return 表示数据集结果的二维表
     */
    @Override
    public DataModel createDataModel(Calculator calculator, int rowCount) {
        return createDataModel();
    }

    @Override
    public void registerNoPrivilege(ArrayList<String> arrayList, String s, String s1) {

    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof BIExcelTableData
                && this.needColumnName == ((BIExcelTableData) obj).needColumnName
                && ComparatorUtils.equals(this.filePath,
                ((BIExcelTableData) obj).filePath);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String[] getColumnNames(TableDataSource tableDataSource) {
        return new String[0];
    }

}