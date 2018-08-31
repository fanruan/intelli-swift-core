package com.fr.swift.source.excel.data;

import com.fr.base.Parameter;
import com.fr.base.ParameterHelper;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.excel.ExcelUtil;
import com.fr.swift.util.Crasher;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author yee
 * @date 2018/4/26
 */
public class ExcelDataModelCreator {
    private static boolean isCsv(String path) {
        return path.endsWith(".csv") || path.endsWith(".CSV");
    }

    public static IExcelDataModel createDataModel(InputStream inputStream, String filePath, Parameter[] parameters) {
        filePath = ParameterHelper.analyze4Templatee(filePath, parameters);
        if (isCsv(filePath)) {
            return new CSVDataModel(inputStream, filePath);
        }
        ExcelUtil.checkHead(filePath);
        return new ExcelDataModel(inputStream, filePath);
    }

    public static IExcelDataModel createDataModel(InputStream inputStream, String filePath) {
        return createDataModel(inputStream, filePath, null);
    }

    public static IExcelDataModel createDataModel(String filePath, Parameter[] parameters) {
        InputStream inputStream = null;
        try {
            if (ExcelUtil.isRemote(filePath)) {
                inputStream = new URL(filePath).openStream();
            } else {
                inputStream = new FileInputStream(filePath);
            }
            return createDataModel(inputStream, filePath, parameters);
        } catch (IOException e) {
            return Crasher.crash(e);
        }
    }

    public static IExcelDataModel createDataModel(String filePath) {
        return createDataModel(filePath, null);
    }

    public static IExcelDataModel createDataModel(String filePath, Parameter[] parameters, String[] columnNames, ColumnTypeConstants.ColumnType[] columnTypes) {
        filePath = ParameterHelper.analyze4Templatee(filePath, parameters);
        if (isCsv(filePath)) {
            return new CSVDataModel(filePath, columnNames, columnTypes);
        }
        InputStream inputStream = null;
        try {
            if (ExcelUtil.isRemote(filePath)) {
                inputStream = new URL(filePath).openStream();
            } else {
                inputStream = new FileInputStream(filePath);
            }
            return new ExcelDataModel(inputStream, filePath, columnNames, columnTypes);
        } catch (IOException e) {
            return Crasher.crash(e);
        }
    }

    public static IExcelDataModel createDataModel(String filePath, String[] columnNames, ColumnTypeConstants.ColumnType[] columnTypes) {
        return createDataModel(filePath, null, columnNames, columnTypes);
    }

    public static IExcelDataModel createDataModel(InputStream inputStream, String filePath, Parameter[] parameters, String[] columnNames, ColumnTypeConstants.ColumnType[] columnTypes) {
        filePath = ParameterHelper.analyze4Templatee(filePath, parameters);
        if (isCsv(filePath)) {
            return new CSVDataModel(inputStream, filePath, columnNames, columnTypes);
        }

        return new ExcelDataModel(inputStream, filePath, columnNames, columnTypes);
    }

    public static IExcelDataModel createDataModel(InputStream inputStream, String filePath, String[] columnNames, ColumnTypeConstants.ColumnType[] columnTypes) {
        return createDataModel(inputStream, filePath, null, columnNames, columnTypes);
    }
}
