package com.fr.bi.stable.data.db.excel;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.file.BIPictureUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.stable.StringUtils;
import com.fr.third.v2.org.apache.poi.openxml4j.opc.OPCPackage;
import com.fr.third.v2.org.apache.poi.openxml4j.opc.PackageAccess;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Young on 2015/7/3.
 */
public class Excel2007Util extends AbstractExcel2007Util {

    public Excel2007Util(String filePath, boolean preview) throws Exception {
        this.preview = preview;
        Object lock = BIPictureUtils.getImageLock(filePath);
        synchronized (lock) {
            File xlsxFile = new File(filePath);
            if (!xlsxFile.exists()) {
                System.err.println("Not found or not a file: " + xlsxFile.getPath());
                return;
            }
            this.xlsxPackage = OPCPackage.open(xlsxFile.getPath(), PackageAccess.READ);
        }
        processFirstSheet();
        if (tempRowDataList.size() == 0) {
            processFirstSheetFromBI();
        }
        // 处理一下单元格合并
        mergeCell();

        // 遍历一下所有的格子
        dealWithSomething();

        //如果是首行含有空值
        for (int i = 0; i < columnNames.length; i++) {
            if (StringUtils.EMPTY.equals(columnNames[i])) {
                columnNames[i] = Inter.getLocText("BI-Field") + (i + 1);
            }
            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~\\s]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(columnNames[i]);
            columnNames[i] = m.replaceAll(StringUtils.EMPTY).trim();
        }

        //如果只有一行数据
        if (columnTypes.length == 0 && columnNames.length > 0) {
            columnTypes = new int[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                columnTypes[i] = 1;
            }
        }
    }

    protected void dealWithSomething() {
        for (int i = 0; i < tempRowDataList.size(); i++) {
            Object[] oneRow = tempRowDataList.get(i);
            currentRowData = new ArrayList<Object>();
            //首行 确定字段名
            if (i == 0) {
                columnCount = oneRow.length;
                columnNames = new String[columnCount];
                for (int j = 0; j < oneRow.length; j++) {
                    String cName = oneRow[j].toString();
                    String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~\\s]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(cName);
                    cName = m.replaceAll(StringUtils.EMPTY).trim();
                    columnNames[j] = cName;
                    if (ComparatorUtils.equals(StringUtils.EMPTY, cName)) {
                        columnNames[j] = Inter.getLocText("BI-Field");
                    }
                }
                createDistinctColumnNames();
            } else if (i == 1) {
                columnTypes = new int[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    String v;
                    try {
                        v=oneRow[j].toString();
                    }catch (Exception e){
                        v=StringUtils.EMPTY;
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
                    if (v.matches("^[+-]?([1-9][0-9]*|0)(\\.[0-9]+)?%?$")) {
                        columnTypes[j] = DBConstant.COLUMN.NUMBER;
                    } else if (dateType) {
                        columnTypes[j] = DBConstant.COLUMN.DATE;
                    } else {
                        columnTypes[j] = DBConstant.COLUMN.STRING;
                    }
                }
                rowDataList.add(currentRowData.toArray());
            } else {
                for (int j = 0; j < columnCount; j++) {
                    String v;
                    try {
                        v=oneRow[j].toString();
                    }catch (Exception e){
                        v=StringUtils.EMPTY;
                    }
                    currentRowData.add(v);
                }
                rowDataList.add(currentRowData.toArray());
            }
        }
    }
}