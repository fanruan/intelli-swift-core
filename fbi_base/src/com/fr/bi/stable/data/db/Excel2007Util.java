package com.fr.bi.stable.data.db;

import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPictureUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.stable.ColumnRow;
import com.fr.stable.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Young on 2015/7/3.
 */
public class Excel2007Util {

    public boolean isEmpty = false;
    private boolean isFirstRow = true;
    private boolean isSecondRow = false;
    private String[] columnNames = new String[0];
    private int[] columnTypes = new int[0];
    private int columnCount = 0;
    private List<Object> currentRowData = new ArrayList<Object>();
    private List<Object[]> rowDataList = new ArrayList<Object[]>();
    private Map<ColumnRow, ColumnRow> mergeCells = new HashMap<ColumnRow, ColumnRow>();
    private boolean preview = false;
    private boolean hasFormula = false;
    private OPCPackage xlsxPackage;
    private List<String> tempData = new ArrayList<String>();

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
        if (columnNames.length == 0) {
            processFirstSheetFromBI();
        }
        //处理一下单元格合并
        mergeCell();

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

    public String[] getColumnNames() {
        return columnNames;
    }

    public int[] getColumnTypes() {
        return columnTypes;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public List<Object[]> getRowDataList() {
        return rowDataList;
    }

    public boolean hasFormula() {
        return hasFormula;
    }

    public void mergeCell() {
        Map<ColumnRow, ColumnRow> merges = mergeCells;
        for (Map.Entry<ColumnRow, ColumnRow> m : merges.entrySet()) {
            ColumnRow s = m.getKey();
            ColumnRow e = m.getValue();
            //如果是横向合并
            if (s.getRow() == e.getRow() && s.getRow() != 0) {
                int mergedColCount = e.getColumn() - s.getColumn();
                for (int i = 0; i < mergedColCount; i++) {
                    Object[] tempArray = rowDataList.get(e.getRow() - 1);
                    tempArray[e.getColumn() - i] = rowDataList.get(e.getRow() - 1)[e.getColumn() - i - 1];
                    rowDataList.set(e.getRow() - 1, tempArray);
                }
            } else if (s.getRow() != 0) {
                int mergedRowCount = e.getRow() - s.getRow();
                for (int j = 0; j < mergedRowCount; j++) {
                    Object[] tempArray = rowDataList.get(e.getRow() - j - 1);
                    tempArray[e.getColumn()] = rowDataList.get(s.getRow() - 1)[e.getColumn()];
                    rowDataList.set(e.getRow() - j - 1, tempArray);
                }
            } else if (s.getRow() == 0) {
                int mergedColCount = e.getColumn() - s.getColumn();
                for (int i = 0; i < mergedColCount; i++) {
                    columnNames[e.getColumn() - i] = columnNames[s.getColumn()];
                }
            }
        }
    }

    public void processSheet(
            StylesTable styles,
            ReadOnlySharedStringsTable strings,
            InputStream sheetInputStream)
            throws IOException, ParserConfigurationException, SAXException {

        InputSource sheetSource = new InputSource(sheetInputStream);
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader sheetParser = saxParser.getXMLReader();
        ContentHandler handler = new MyXSSFSheetHandler(styles, strings);
        sheetParser.setContentHandler(handler);
        sheetParser.parse(sheetSource);
    }

    public void processFirstSheet() throws Exception {
        ReadOnlySharedStringsTable sst = new ReadOnlySharedStringsTable(this.xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        InputStream sheetInputStream = xssfReader.getSheet("rId1");
        InputSource sheetSource = new InputSource(sheetInputStream);
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader sheetParser = saxParser.getXMLReader();
        ContentHandler handler = new MyXSSFSheetHandler(styles, sst);
        sheetParser.setContentHandler(handler);
        sheetParser.parse(sheetSource);
        sheetInputStream.close();
    }

    //组件中导出的excel比较特殊
    public void processFirstSheetFromBI() throws Exception {
        ReadOnlySharedStringsTable sst = new ReadOnlySharedStringsTable(this.xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        InputStream sheetInputStream = xssfReader.getSheet("rId3");
        InputSource sheetSource = new InputSource(sheetInputStream);
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader sheetParser = saxParser.getXMLReader();
        ContentHandler handler = new MyXSSFSheetHandler(styles, sst);
        sheetParser.setContentHandler(handler);
        sheetParser.parse(sheetSource);
        sheetInputStream.close();
    }

    public void process()
            throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {

        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);

        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        int index = 0;
        while (iter.hasNext()) {
            InputStream stream = iter.next();
            String sheetName = iter.getSheetName();
            processSheet(styles, strings, stream);
            stream.close();
            ++index;
        }
    }

    enum xssfDataType {
        BOOL,
        ERROR,
        FORMULA,
        INLINESTR,
        SSTINDEX,
        NUMBER,
    }

    class MyXSSFSheetHandler extends DefaultHandler {

        private final DataFormatter formatter;
        private StylesTable stylesTable;
        private ReadOnlySharedStringsTable sharedStringsTable;
        private boolean vIsOpen;
        private xssfDataType nextDataType;
        private short formatIndex;
        private String formatString;
        private int thisColumn = -1;
        private int lastColumnNumber = -1;
        private StringBuffer value;
        private String cellValue = null;

        public MyXSSFSheetHandler(StylesTable styles, ReadOnlySharedStringsTable strings) {
            this.stylesTable = styles;
            this.sharedStringsTable = strings;
            this.value = new StringBuffer();
            this.nextDataType = xssfDataType.NUMBER;
            this.formatter = new DataFormatter();
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if ("inlineStr".equals(name) || "v".equals(name)) {
                vIsOpen = true;
                value.setLength(0);
            } else if ("c".equals(name)) {
                String r = attributes.getValue("r");
                int firstDigit = -1;
                for (int c = 0; c < r.length(); ++c) {
                    if (Character.isDigit(r.charAt(c))) {
                        firstDigit = c;
                        break;
                    }
                }
                thisColumn = nameToColumn(r.substring(0, firstDigit));
                this.nextDataType = xssfDataType.NUMBER;
                this.formatIndex = -1;
                this.formatString = null;
                String cellType = attributes.getValue("t");
                String cellStyleStr = attributes.getValue("s");
                if ("b".equals(cellType)){
                    nextDataType = xssfDataType.BOOL;
                }
                else if ("e".equals(cellType)) {
                    nextDataType = xssfDataType.ERROR;
                } else if ("inlineStr".equals(cellType)) {
                    nextDataType = xssfDataType.INLINESTR;
                } else if ("s".equals(cellType)) {
                    nextDataType = xssfDataType.SSTINDEX;
                } else if ("str".equals(cellType)) {
                    nextDataType = xssfDataType.FORMULA;
                }
                if (cellStyleStr != null) {
                    int styleIndex = Integer.parseInt(cellStyleStr);
                    XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                    this.formatIndex = style.getDataFormat();
                    this.formatString = style.getDataFormatString();
                    if (this.formatString == null) {
                        this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                    }
                }
                isEmpty = false;
            } else if ("mergeCell".equals(name)) {
                String merge = attributes.getValue("ref");
                String[] merged = merge.split(":");
                String s = merged[0];
                String e = merged[1];
                ColumnRow start = ColumnRow.valueOf(s);
                ColumnRow end = ColumnRow.valueOf(e);
                mergeCells.put(start, end);
            }
        }

        public void processSSTIndex() {
            String sstIndex = value.toString();
            try {
                int idx = Integer.parseInt(sstIndex);
                XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                cellValue = rtss.toString();
            } catch (NumberFormatException ex) {
                BILogger.getLogger().error(ex.getMessage(), ex);
            }
        }

        public void processNumber() {
            String n = value.toString();
            boolean isDateFormat = this.formatString != null && (this.formatString.contains("yy") || this.formatString.contains("dd") || this.formatString.contains("mm"));
            if (isDateFormat) {
                try {
                    Date date = HSSFDateUtil.getJavaDate(Double.parseDouble(n));
                    SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
                    cellValue = dformat.format(date);
                } catch (Exception e) {
                    cellValue = n;
                }
            } else {
                cellValue = n;
            }
        }

        public void processElement() {
            switch (nextDataType) {
                case BOOL:
                    char first = value.charAt(0);
                    cellValue = first == '0' ? "FALSE" : "TRUE";
                    break;
                case ERROR:
                    cellValue = value.toString();
                    break;
                case FORMULA:
                    cellValue = value.toString();
                    break;
                case INLINESTR:
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    cellValue = rtsi.toString();
                    break;
                case SSTINDEX:
                    processSSTIndex();
                    break;
                case NUMBER:
                    processNumber();
                    break;
                default:
                    cellValue = StringUtils.EMPTY;
                    break;
            }
            if (lastColumnNumber == -1) {
                lastColumnNumber = 0;
            }

            for (int i = lastColumnNumber; i < thisColumn; ++i) {
                if (tempData.size() < thisColumn) {
                    tempData.add(StringUtils.EMPTY);
                }
            }
            tempData.add(cellValue);

            if (thisColumn > -1) {
                lastColumnNumber = thisColumn;
            }
        }

        public void processColumnType() {
            if (isSecondRow) {
                isSecondRow = false;
                columnTypes = new int[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    if (currentRowData.size() < columnCount) {
                        for (int j = 0; j < columnCount - currentRowData.size(); j++) {
                            currentRowData.add(StringUtils.EMPTY);
                        }
                    }
                    String v = currentRowData.get(i).toString();
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
                        columnTypes[i] = DBConstant.COLUMN.NUMBER;
                    } else if (dateType) {
                        columnTypes[i] = DBConstant.COLUMN.DATE;
                    } else {
                        columnTypes[i] = DBConstant.COLUMN.STRING;
                    }
                }
            }
        }

        public void processColumnName() {
            if (isFirstRow) {
                isFirstRow = false;
                isSecondRow = true;
                columnNames = currentRowData.toArray(new String[columnCount]);
                //处理字段重名
                for (int i = 0; i < columnNames.length; i++) {
                    int count = 1;
                    for (int j = 0; j < columnNames.length; j++) {
                        if (ComparatorUtils.equals(columnNames[i], columnNames[j]) && i != j) {
                            columnNames[j] += count;
                            count++;
                        }
                    }
                }
            }
        }

        public void processRow() {
            if (isFirstRow) {
                columnCount = tempData.size();
                for (int i = 0; i < tempData.size(); i++) {
                    try {
                        currentRowData.add(tempData.get(i));
                    } catch (Exception e) {
                        currentRowData.add(StringUtils.EMPTY);
                    }
                }
            } else {
                for (int i = 0; i < columnCount; i++) {
                    if (i < tempData.size()) {
                        try {
                            currentRowData.add(tempData.get(i));
                        } catch (Exception e) {
                            currentRowData.add(StringUtils.EMPTY);
                        }
                    } else {
                        currentRowData.add(StringUtils.EMPTY);
                    }
                }
            }

            //从第二行来读取数据类型
            processColumnType();

            //添加非第一行到所有数据的List
            if (!isFirstRow) {
                rowDataList.add(currentRowData.toArray(new Object[columnCount]));
            }

            //第一行获取列名
            processColumnName();

            currentRowData = new ArrayList<Object>();
            tempData = new ArrayList<String>();

            lastColumnNumber = -1;
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            if (preview && rowDataList.size() > BIBaseConstant.PREVIEW_COUNT) {
                return;
            }
            cellValue = null;
            if ("v".equals(name)) {
                processElement();
            } else if ("row".equals(name)) {
                processRow();
            }

        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (vIsOpen) {
                value.append(ch, start, length);
            }
        }

        private int nameToColumn(String name) {
            int column = -1;
            for (int i = 0; i < name.length(); ++i) {
                int c = name.charAt(i);
                column = (column + 1) * 26 + c - 'A';
            }
            return column;
        }
    }
}