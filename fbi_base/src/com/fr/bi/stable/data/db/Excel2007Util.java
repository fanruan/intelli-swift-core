package com.fr.bi.stable.data.db;

import com.fr.bi.stable.constant.DBConstant;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.file.BIPictureUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.stable.ColumnRow;
import com.fr.stable.StringUtils;
import com.fr.third.v2.org.apache.poi.hssf.usermodel.HSSFDateUtil;
import com.fr.third.v2.org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import com.fr.third.v2.org.apache.poi.openxml4j.opc.OPCPackage;
import com.fr.third.v2.org.apache.poi.openxml4j.opc.PackageAccess;
import com.fr.third.v2.org.apache.poi.ss.usermodel.BuiltinFormats;
import com.fr.third.v2.org.apache.poi.ss.usermodel.DataFormatter;
import com.fr.third.v2.org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import com.fr.third.v2.org.apache.poi.xssf.eventusermodel.XSSFReader;
import com.fr.third.v2.org.apache.poi.xssf.model.StylesTable;
import com.fr.third.v2.org.apache.poi.xssf.usermodel.XSSFCellStyle;
import com.fr.third.v2.org.apache.poi.xssf.usermodel.XSSFRichTextString;
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
    private String[] columnNames = new String[0];
    private int[] columnTypes = new int[0];
    private int columnCount = 0;
    private List<Object> currentRowData = new ArrayList<Object>();
    private List<Object[]> rowDataList = new ArrayList<Object[]>();
    private List<Object[]> tempRowDataList = new ArrayList<Object[]>();
    private Map<ColumnRow, ColumnRow> mergeCells = new HashMap<ColumnRow, ColumnRow>();
    private boolean preview = false;
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

    public Map<ColumnRow, ColumnRow> getMergeInfos() {
        return mergeCells;
    }

    public void mergeCell() {
        Map<ColumnRow, ColumnRow> merges = mergeCells;
        try {
            for (Map.Entry<ColumnRow, ColumnRow> m : merges.entrySet()) {
                ColumnRow s = m.getKey();
                ColumnRow e = m.getValue();
                //如果是横向合并
                if (s.getRow() == e.getRow()) {
                    int mergedColCount = e.getColumn() - s.getColumn();
                    for (int i = 0; i < mergedColCount; i++) {
                        Object[] tempArray = tempRowDataList.get(e.getRow());
                        if (tempArray.length < e.getColumn() + 1) {
                            Object[] tArray = new Object[e.getColumn() + 1];
                            for (int k = 0; k < tempArray.length; k++) {
                                tArray[k] = tempArray[k];
                            }
                            tArray[e.getColumn() - i] = tempRowDataList.get(e.getRow())[s.getColumn()];
                            tempRowDataList.set(e.getRow(), tArray);
                        } else {
                            tempArray[e.getColumn() - i] = tempRowDataList.get(e.getRow())[s.getColumn()];
                            tempRowDataList.set(e.getRow(), tempArray);
                        }
                    }
                } else {
                    int mergedRowCount = e.getRow() - s.getRow();
                    for (int j = 0; j < mergedRowCount; j++) {
                        Object[] tempArray = tempRowDataList.get(e.getRow() - j);
                        if (tempArray.length < e.getColumn() + 1) {
                            Object[] tArray = new Object[e.getColumn() + 1];
                            for (int k = 0; k < tempArray.length; k++) {
                                tArray[k] = tempArray[k];
                            }
                            tArray[e.getColumn()] = tempRowDataList.get(s.getRow())[e.getColumn()];
                            tempRowDataList.set(e.getRow() - j, tArray);
                        } else {
                            tempArray[e.getColumn()] = tempRowDataList.get(s.getRow())[e.getColumn()];
                            tempRowDataList.set(e.getRow() - j, tempArray);
                        }
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }

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
        while (iter.hasNext()) {
            InputStream stream = iter.next();
            processSheet(styles, strings, stream);
            stream.close();
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

        private StylesTable stylesTable;
        private ReadOnlySharedStringsTable sharedStringsTable;
        private boolean vIsOpen;
        private boolean fIsOpen;
        private boolean isIsOpen;
        private boolean hfIsOpen;
        private xssfDataType nextDataType;
        private short formatIndex;
        private String formatString;
        private final DataFormatter formatter;
        private StringBuffer value;
        private StringBuffer formula;
        private StringBuffer headerFooter;
        private boolean formulasNotResults;
        private String cellValue;
        private int thisColumn = -1;
        private int lastColumnNumber = -1;
        private final int PERCENT = 100;

        public MyXSSFSheetHandler(StylesTable styles, ReadOnlySharedStringsTable strings, DataFormatter dataFormatter) {
            this.value = new StringBuffer();
            this.formula = new StringBuffer();
            this.headerFooter = new StringBuffer();
            this.stylesTable = styles;
            this.sharedStringsTable = strings;
            this.nextDataType = xssfDataType.NUMBER;
            this.formatter = dataFormatter;
            this.formulasNotResults = false;
        }

        public MyXSSFSheetHandler(StylesTable styles, ReadOnlySharedStringsTable strings) {
            this(styles, strings, new DataFormatter());
        }

        private boolean isTextTag(String name) {
            return "v".equals(name) ? true : ("inlineStr".equals(name) ? true : "t".equals(name) && this.isIsOpen);
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if (this.isTextTag(name)) {
                this.vIsOpen = true;
                this.value.setLength(0);
            } else if ("is".equals(name)) {
                this.isIsOpen = true;
            } else {
                String cellType;
                String cellStyleStr;
                if ("f".equals(name)) {
                    this.formula.setLength(0);
                    if (this.nextDataType == xssfDataType.NUMBER) {
                        this.nextDataType = xssfDataType.FORMULA;
                    }

                    cellType = attributes.getValue("t");
                    if (cellType != null && cellType.equals("shared")) {
                        cellStyleStr = attributes.getValue("ref");
                        if (cellStyleStr != null) {
                            this.fIsOpen = true;
                        } else if (this.formulasNotResults) {
                            System.err.println("Warning - shared formulas not yet supported!");
                        }
                    } else {
                        this.fIsOpen = true;
                    }
                } else if (!"oddHeader".equals(name) && !"evenHeader".equals(name) && !"firstHeader".equals(name) && !"firstFooter".equals(name) && !"oddFooter".equals(name) && !"evenFooter".equals(name)) {
                    if ("row".equals(name)) {
//                        int cellType1 = Integer.parseInt(attributes.getValue("r")) - 1;
//                        this.output.startRow(cellType1);
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
                        cellType = attributes.getValue("t");
                        cellStyleStr = attributes.getValue("s");
                        if ("b".equals(cellType)) {
                            this.nextDataType = xssfDataType.BOOL;
                        } else if ("e".equals(cellType)) {
                            this.nextDataType = xssfDataType.ERROR;
                        } else if ("inlineStr".equals(cellType)) {
                            this.nextDataType = xssfDataType.INLINESTR;
                        } else if ("s".equals(cellType)) {
                            this.nextDataType = xssfDataType.SSTINDEX;
                        } else if ("str".equals(cellType)) {
                            this.nextDataType = xssfDataType.FORMULA;
                        } else if (cellStyleStr != null) {
                            int styleIndex1 = Integer.parseInt(cellStyleStr);
                            XSSFCellStyle style = this.stylesTable.getStyleAt(styleIndex1);
                            this.formatIndex = style.getDataFormat();
                            this.formatString = style.getDataFormatString();
                            if (this.formatString == null) {
                                this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                            }
                        }
                    } else if ("mergeCell".equals(name)) {
                        String merge = attributes.getValue("ref");
                        String[] merged = merge.split(":");
                        String s = merged[0];
                        String e = merged[1];
                        ColumnRow start = ColumnRow.valueOf(s);
                        ColumnRow end = ColumnRow.valueOf(e);
                        mergeCells.put(start, end);
                    }
                } else {
                    this.hfIsOpen = true;
                    this.headerFooter.setLength(0);
                }
            }

        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            if (this.isTextTag(name)) {
                this.vIsOpen = false;
                switch (nextDataType) {
                    case BOOL:
                        char first = this.value.charAt(0);
                        cellValue = first == 48 ? "FALSE" : "TRUE";
                        break;
                    case ERROR:
                        cellValue = "ERROR:" + this.value.toString();
                        break;
                    case FORMULA:
                        if (this.formulasNotResults) {
                            cellValue = this.formula.toString();
                        } else {
                            String rtsi1 = this.value.toString();
                            if (this.formatString != null) {
                                try {
                                    double sstIndex1 = Double.parseDouble(rtsi1);
                                    cellValue = this.formatter.formatRawCellContents(sstIndex1, this.formatIndex, this.formatString);
                                } catch (NumberFormatException var11) {
                                    cellValue = rtsi1;
                                }
                            } else {
                                cellValue = rtsi1;
                            }
                        }
                        break;
                    case INLINESTR:
                        XSSFRichTextString rtsi = new XSSFRichTextString(this.value.toString());
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
            } else if ("f".equals(name)) {
                this.fIsOpen = false;
            } else if ("is".equals(name)) {
                this.isIsOpen = false;
            } else if ("row".equals(name)) {
                tempRowDataList.add(tempData.toArray());
                tempData = new ArrayList<String>();
                lastColumnNumber = -1;
            } else if (!"oddHeader".equals(name) && !"evenHeader".equals(name) && !"firstHeader".equals(name)) {
                if ("oddFooter".equals(name) || "evenFooter".equals(name) || "firstFooter".equals(name)) {
                    this.hfIsOpen = false;
                }
            } else {
                this.hfIsOpen = false;
            }
        }

        public void processSSTIndex() {
            String sstIndex = value.toString();
            try {
                int idx = Integer.parseInt(sstIndex);
                XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                cellValue = rtss.toString();
            } catch (NumberFormatException ex) {
                BILoggerFactory.getLogger().error(ex.getMessage(), ex);
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
            } else if (this.formatString != null && this.formatString.contains("%")) {
                cellValue = this.formatter.formatRawCellContents(Double.parseDouble(value.toString()) * PERCENT, this.formatIndex, "") + "%";
            } else {
                cellValue = this.formatter.formatRawCellContents(Double.parseDouble(value.toString()), this.formatIndex, "");
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            if (this.vIsOpen) {
                this.value.append(ch, start, length);
            }

            if (this.fIsOpen) {
                this.formula.append(ch, start, length);
            }

            if (this.hfIsOpen) {
                this.headerFooter.append(ch, start, length);
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