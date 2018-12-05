package com.fr.swift.source.excel;

import com.fr.base.AbstractTableData;
import com.fr.data.TableDataSource;
import com.fr.general.data.DataModel;
import com.fr.script.Calculator;
import com.fr.stable.ParameterProvider;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by zcf on 2016/11/21.
 */
public abstract class AbstractExcelTableData extends AbstractTableData {
    protected String filePath = null;

    protected boolean needColumnName = true;
    protected String[] columnNames;
    protected ColumnType[] columnTypes;
    protected InputStream inputStream;

    public AbstractExcelTableData(String filePath) {
        this.filePath = filePath;
    }

    public AbstractExcelTableData(String filePath, String[] columnNames, ColumnType[] columnTypes) {
        this.filePath = filePath;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    public AbstractExcelTableData(String filePath, InputStream inputStream) {
        this.filePath = filePath;
        this.inputStream = inputStream;
    }

    public AbstractExcelTableData(String filePath, InputStream inputStream, String[] columnNames, ColumnType[] columnTypes) {
        this.filePath = filePath;
        this.inputStream = inputStream;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    /**
     * 创建数据表格
     */
    public abstract DataModel createDataModel();

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
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String[] getColumnNames(TableDataSource tableDataSource) {
        return new String[0];
    }
}
