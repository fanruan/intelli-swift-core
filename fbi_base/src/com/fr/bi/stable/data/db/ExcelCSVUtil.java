package com.fr.bi.stable.data.db;

import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.excel.CSVTokenizer;
import com.fr.bi.stable.utils.file.BIPictureUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.stable.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Young's on 2016/7/26.
 */
public class ExcelCSVUtil {

    private String[] columnNames = new String[0];
    private int[] columnTypes = new int[0];
    private int columnCount = 0;
    private List<Object> currentRowData = new ArrayList<Object>();
    private List<Object[]> rowDataList = new ArrayList<Object[]>();
    private List<Object[]> tempRowDataList = new ArrayList<Object[]>();

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public int[] getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(int[] columnTypes) {
        this.columnTypes = columnTypes;
    }

    public List<Object[]> getRowDataList() {
        return rowDataList;
    }

    public void setRowDataList(List<Object[]> rowDataList) {
        this.rowDataList = rowDataList;
    }

    public ExcelCSVUtil(String filePath, boolean isPreview) throws Exception {
        Object lock = BIPictureUtils.getImageLock(filePath);
        synchronized (lock) {
            BufferedReader r = null;
            try {
                r = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), codeString(filePath)));
                int row = 0;
                while (true) {
                    String ln = r.readLine();
                    if (ln == null || (isPreview && row > BIBaseConstant.PREVIEW_COUNT)) {
                        break;
                    }
                    for (CSVTokenizer it = new CSVTokenizer(ln); it.hasMoreTokens(); ) {
                        try {
                            String val = it.nextToken();
                            currentRowData.add(val);
                        } catch (Exception e) {
                            BILogger.getLogger().error(e.getMessage());
                        }
                    }
                    tempRowDataList.add(currentRowData.toArray());
                    currentRowData = new ArrayList<Object>();
                    row++;
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage());
            } finally {
                if (r != null) {
                    r.close();
                }
            }
        }
        dealWithSomething();
    }

    private void createDistinctColumnNames() {
        for (int i = 0; i < columnNames.length; i++) {
            int index = 1;
            String columnName = columnNames[i];
            for (int j = 0; j < columnNames.length; j++) {
                if (i != j && ComparatorUtils.equals(columnName, columnNames[j])) {
                    columnNames[j] = columnName + index;
                    index++;
                }
            }
        }
    }

    private void dealWithSomething() {
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
                    String v = StringUtils.EMPTY;
                    if (oneRow.length > j) {
                        v = oneRow[j].toString();
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
                    String v = StringUtils.EMPTY;
                    if (oneRow.length > j) {
                        v = oneRow[j].toString();
                    }
                    currentRowData.add(v);
                }
                rowDataList.add(currentRowData.toArray());
            }
        }
    }

    public static String codeString(String filePath) throws Exception {

        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(filePath));
        int p = (bin.read() << 8) + bin.read();
        String code = null;
        //其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            case 0x5c75:
                code = "ANSI|ASCII";
                break;
            default:
                code = "GBK";
        }
        return code;
    }
}