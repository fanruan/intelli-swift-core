package com.fr.swift.source.excel;

import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.stable.ColumnRow;
import com.fr.stable.StringUtils;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Young's on 2016/7/26.
 */
public class ExcelCSVReader extends AbstractExcelReader {
    private static final int READLENGTH = 8;
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ExcelCSVReader.class);
    private String[] columnNames = new String[0];
    private ColumnType[] columnTypes = new ColumnType[0];
    private int columnCount = 0;
    private List<Object> currentRowData = new ArrayList<Object>();
    private List<Object[]> rowDataList = new ArrayList<Object[]>();
    private List<Object[]> tempRowDataList = new ArrayList<Object[]>();
    private BufferedReader reader;
    private boolean end = false;

    public ExcelCSVReader(InputStream inputStream, String filePath, boolean isPreview) throws Exception {
        try {
            readCsv(inputStream, filePath, isPreview);
        } catch (Exception e) {
            readCsv(null, filePath, isPreview);
        }
    }

    public ExcelCSVReader(String filePath, boolean isPreview) throws Exception {
        this(null, filePath, isPreview);
    }

    public static String codeString(String filePath) throws Exception {
        InputStream inputStream = null;
        if (filePath.startsWith("http")) {
            inputStream = new URL(filePath).openStream();
        } else {
            inputStream = new FileInputStream(filePath);
        }
        BufferedInputStream bin = new BufferedInputStream(inputStream);
        int p = (bin.read() << READLENGTH) + bin.read();
        String code = null;
        //其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
        switch (p) {
            case ExcelConstant.CSV_CODE_TYPE.UTF8:
                code = "UTF-8";
                break;
            case ExcelConstant.CSV_CODE_TYPE.UNICODE:
                code = "Unicode";
                break;
            case ExcelConstant.CSV_CODE_TYPE.UTF16BE:
                code = "UTF-16BE";
                break;
            case ExcelConstant.CSV_CODE_TYPE.ANSIORASCII:
                code = "ANSI|ASCII";
                break;
            default:
                code = "GBK";
        }
        return code;
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public ColumnType[] getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(ColumnType[] columnTypes) {
        this.columnTypes = columnTypes;
    }

    public boolean isEnd() {
        return end;
    }

    @Override
    Map<ColumnRow, ColumnRow> getMergeInfos() {
        return null;
    }

    @Override
    public List<Object[]> getRowDataList() {
        return Collections.unmodifiableList(rowDataList);
    }

    public void setRowDataList(List<Object[]> rowDataList) {
        this.rowDataList = rowDataList;
    }

    public Object[] read() throws IOException {
        String ln = reader.readLine();
        if (ln == null) {
            end = true;
            return new Object[0];
        }
        currentRowData.clear();
        for (CSVTokenizer it = new CSVTokenizer(ln); it.hasMoreTokens(); ) {
            try {
                currentRowData.add(it.nextToken());
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e.getMessage());
            }
        }
        return currentRowData.toArray();
    }

    private void readCsv(InputStream inputStream, String filePath, boolean isPreview) throws Exception {
        if (null == inputStream) {
            if (-1 != filePath.indexOf("http")) {
                URL url = new URL(filePath);
                inputStream = url.openConnection().getInputStream();
            } else {
                inputStream = new FileInputStream(filePath);
            }
        }
        if (!isPreview) {
            reader = new BufferedReader(new InputStreamReader(inputStream, codeString(filePath)));
            // 第一行是columnNames
            read();
            return;
        }
        Object lock = ExcelFileLockUtils.getImageLock(filePath);
        synchronized (lock) {
            BufferedReader r = null;
            try {
                r = new BufferedReader(new InputStreamReader(inputStream, codeString(filePath)));
                int row = 0;
                while (true) {
                    String ln = r.readLine();
                    if (ln == null || row > ExcelConstant.PREVIEW_COUNT) {
                        break;
                    }
                    for (CSVTokenizer it = new CSVTokenizer(ln); it.hasMoreTokens(); ) {
                        try {
                            currentRowData.add(it.nextToken());
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().error(e.getMessage());
                        }
                    }
                    tempRowDataList.add(currentRowData.toArray());
                    currentRowData.clear();
                    row++;
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e.getMessage());
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
                        columnNames[j] = Inter.getLocText("BI-Basic_Field");
                    }
                }
                createDistinctColumnNames();
            } else if (i == 1) {
                initFieldTypes(oneRow);
            } else {
                for (int j = 0; j < columnCount; j++) {
                    String v = StringUtils.EMPTY;
                    if (oneRow.length > j) {
                        v = oneRow[j].toString().trim();
                    }
                    currentRowData.add(v);
                }
                rowDataList.add(currentRowData.toArray());
            }
        }
    }

    private void initFieldTypes(Object[] oneRow) {
        columnTypes = new ColumnType[columnCount];
        for (int j = 0; j < columnCount; j++) {
            String v = StringUtils.EMPTY;
            if (oneRow.length > j) {
                v = oneRow[j].toString().trim();
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