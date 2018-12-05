package com.fr.swift.source.excel;

import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.GeneralUtils;
import com.fr.general.Inter;
import com.fr.stable.StringUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.third.v2.org.apache.poi.openxml4j.opc.OPCPackage;
import com.fr.third.v2.org.apache.poi.openxml4j.opc.PackageAccess;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Young on 2015/7/3.
 */
@Deprecated
public class Excel2007Reader extends AbstractExcel2007Reader {

    public Excel2007Reader(String filePath, boolean preview) throws Exception {
        this.preview = preview;
        Object lock = ExcelFileLockUtils.getImageLock(filePath);
        synchronized (lock) {
            File xlsxFile = new File(filePath);
            if (!xlsxFile.exists()) {
                SwiftLoggers.getLogger().error("Not found or not a file: " + xlsxFile.getPath());
                return;
            }
            this.xlsxPackage = OPCPackage.open(xlsxFile.getPath(), PackageAccess.READ);
        }
        processFirstSheet();
        if (tempRowDataList.size() == 0) {
            processFirstSheetFromBI();
        }
        //读取完后关闭文件
        this.xlsxPackage.close();

        // 处理一下单元格合并
        mergeCell();

        // 遍历一下所有的格子
        dealWithSomething();

        //如果是首行含有空值
        for (int i = 0; i < columnNames.length; i++) {
            if (StringUtils.EMPTY.equals(columnNames[i])) {
                columnNames[i] = Inter.getLocText("BI-Excel_Field") + (i + 1);
            }
            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~\\s]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(columnNames[i]);
            columnNames[i] = m.replaceAll(StringUtils.EMPTY).trim();
        }

        //如果只有一行数据
        if (columnTypes.length == 0 && columnNames.length > 0) {
            columnTypes = new ColumnType[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                columnTypes[i] = ColumnType.STRING;
            }
        }
    }

    @Override
    protected void dealWithSomething() {
        for (int i = 0; i < tempRowDataList.size(); i++) {
            Object[] oneRow = tempRowDataList.get(i);
            if (oneRow.length > columnCount) {
                columnCount = oneRow.length;
            }
        }
        for (int i = 0; i < tempRowDataList.size(); i++) {
            Object[] oneRow = tempRowDataList.get(i);
            currentRowData = new ArrayList<Object>();
            //首行 确定字段名
            if (i == 0) {
                dealWithExcelFieldName(oneRow);
            } else if (i == 1) {
                dealWithExcelFieldType(oneRow);
            } else {
                for (int j = 0; j < columnCount; j++) {
                    String v;
                    try {
                        v = GeneralUtils.objectToString(oneRow[j]);
                    } catch (Exception e) {
                        v = StringUtils.EMPTY;
                    }
                    currentRowData.add(v);
                }
                rowDataList.add(currentRowData.toArray());
            }
        }
    }

    private void dealWithExcelFieldName(Object[] oneRow) {
        columnNames = new String[columnCount];
        for (int j = 0; j < columnCount; j++) {
            String cName;
            try {
                cName = GeneralUtils.objectToString(oneRow[j]);
            } catch (Exception e) {
                cName = StringUtils.EMPTY;
            }
            String regEx = ExcelConstant.ILLEGAL_REG;
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(cName);
            cName = m.replaceAll(StringUtils.EMPTY).trim();
            columnNames[j] = cName;
            if (ComparatorUtils.equals(StringUtils.EMPTY, cName)) {
                columnNames[j] = Inter.getLocText("BI-Excel_Field");
            }
        }
        createDistinctColumnNames();
    }

    private void dealWithExcelFieldType(Object[] oneRow) {
        columnTypes = new ColumnType[columnCount];
        for (int j = 0; j < columnCount; j++) {
            String v;
            try {
                v = GeneralUtils.objectToString(oneRow[j]);
            } catch (Exception e) {
                v = StringUtils.EMPTY;
            }
            currentRowData.add(v);
            boolean dateType = false;
            try {
                Date date = DateUtils.string2Date(v, true);
                if (date != null) {
                    dateType = true;
                }
            } catch (Exception e) {
                dateType = false;
            }
            if (v.matches(ExcelConstant.NUMBER_REG)) {
                columnTypes[j] = ColumnType.NUMBER;
            } else if (dateType) {
                columnTypes[j] = ColumnType.DATE;
            } else {
                columnTypes[j] = ColumnType.STRING;
            }
        }
        rowDataList.add(currentRowData.toArray());
    }
}