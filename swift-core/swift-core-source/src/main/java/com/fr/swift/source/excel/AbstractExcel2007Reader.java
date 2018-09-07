package com.fr.swift.source.excel;

import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.stable.ColumnRow;
import com.fr.stable.StringUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.third.v2.org.apache.poi.hssf.usermodel.HSSFDateUtil;
import com.fr.third.v2.org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import com.fr.third.v2.org.apache.poi.openxml4j.opc.OPCPackage;
import com.fr.third.v2.org.apache.poi.ss.usermodel.BuiltinFormats;
import com.fr.third.v2.org.apache.poi.ss.usermodel.DataFormatter;
import com.fr.third.v2.org.apache.poi.ss.util.NumberToTextConverter;
import com.fr.third.v2.org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import com.fr.third.v2.org.apache.poi.xssf.eventusermodel.XSSFReader;
import com.fr.third.v2.org.apache.poi.xssf.model.StylesTable;
import com.fr.third.v2.org.apache.poi.xssf.usermodel.XSSFCellStyle;
import com.fr.third.v2.org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zcf on 2016/11/21.
 */
@Deprecated
public abstract class AbstractExcel2007Reader extends AbstractExcelReader {
    public boolean isEmpty = false;
    protected String[] columnNames = new String[0];
    protected ColumnType[] columnTypes = new ColumnType[0];
    protected int columnCount = 0;
    protected List<Object> currentRowData = new ArrayList<Object>();
    protected List<Object[]> rowDataList = new ArrayList<Object[]>();
    protected List<Object[]> tempRowDataList = new ArrayList<Object[]>();
    protected boolean preview = false;
    protected OPCPackage xlsxPackage;
    private Map<ColumnRow, ColumnRow> mergeCells = new HashMap<ColumnRow, ColumnRow>();
    private List<String> tempData = new ArrayList<String>();

    public AbstractExcel2007Reader() {
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public ColumnType[] getColumnTypes() {
        return columnTypes;
    }

    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public List<Object[]> getRowDataList() {
        return rowDataList;
    }

    @Override
    public Map<ColumnRow, ColumnRow> getMergeInfos() {
        return mergeCells;
    }

    public void mergeCell() {
        Map<ColumnRow, ColumnRow> merges = mergeCells;
        try {
            for (Map.Entry<ColumnRow, ColumnRow> m : merges.entrySet()) {
                ColumnRow s = m.getKey();
                ColumnRow e = m.getValue();
                if (s.getRow() == e.getRow()) {
                    //如果是横向合并
                    rowMerge(s, e);
                } else if (s.getColumn() == e.getColumn()) {
                    //纵向合并
                    columnMerge(s, e);
                } else {
                    //横纵均合并
                    int mergedRowCount = e.getRow() - s.getRow();
                    Object v;
                    try {//处理为空的合并单元格
                        v = tempRowDataList.get(s.getRow())[s.getColumn()];
                    } catch (Exception ex) {
                        v = StringUtils.EMPTY;
                    }
                    for (int i = 0; i <= mergedRowCount; i++) {
                        ColumnRow start = ColumnRow.valueOf(s.getColumn(), s.getRow() + i);
                        ColumnRow end = ColumnRow.valueOf(e.getColumn(), s.getRow() + i);
                        rowColMerge(start, end, v);
                    }
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        }

    }

    private void rowMerge(ColumnRow s, ColumnRow e) {
        int mergedColCount = e.getColumn() - s.getColumn();
        for (int i = 0; i < mergedColCount; i++) {
            Object[] tempArray = tempRowDataList.get(e.getRow());
            if (tempArray.length < e.getColumn() + 1) {
                Object[] tArray = new Object[e.getColumn() + 1];
                for (int k = 0; k < tempArray.length; k++) {
                    tArray[k] = tempArray[k];
                }
                Object v;
                try {//处理为空的合并单元格
                    v = tempRowDataList.get(s.getRow())[s.getColumn()];
                } catch (Exception ex) {
                    v = StringUtils.EMPTY;
                    tArray[s.getColumn()] = v;
                }
                tArray[e.getColumn() - i] = v;
                tempRowDataList.set(e.getRow(), tArray);
            } else {
                tempArray[e.getColumn() - i] = tempRowDataList.get(s.getRow())[s.getColumn()];
                tempRowDataList.set(e.getRow(), tempArray);
            }
        }
    }

    private void columnMerge(ColumnRow s, ColumnRow e) {
        int mergedRowCount = e.getRow() - s.getRow();
        for (int j = 0; j < mergedRowCount; j++) {
            Object[] tempArray = tempRowDataList.get(e.getRow() - j);
            if (tempArray.length < e.getColumn() + 1) {
                Object[] tArray = new Object[e.getColumn() + 1];
                for (int k = 0; k < tempArray.length; k++) {
                    tArray[k] = tempArray[k];
                }
                Object v;
                try {//处理为空的合并单元格
                    v = tempRowDataList.get(s.getRow())[s.getColumn()];
                } catch (Exception ex) {
                    v = StringUtils.EMPTY;
                    Object[] sArray = tempRowDataList.get(s.getRow());
                    Object[] temArray = new Object[e.getColumn() + 1];
                    for (int k = 0; k < sArray.length; k++) {
                        temArray[k] = sArray[k];
                    }
                    temArray[s.getColumn()] = v;
                    tempRowDataList.set(s.getRow(), temArray);
                }
                tArray[e.getColumn()] = v;
                tempRowDataList.set(e.getRow() - j, tArray);
            } else {
                tempArray[e.getColumn()] = tempRowDataList.get(s.getRow())[s.getColumn()];
                tempRowDataList.set(e.getRow() - j, tempArray);
            }
        }
    }

    private void rowColMerge(ColumnRow s, ColumnRow e, Object value) {
        int mergedColCount = e.getColumn() - s.getColumn();
        for (int i = 0; i < mergedColCount; i++) {
            Object[] tempArray = tempRowDataList.get(e.getRow());
            if (tempArray.length < e.getColumn() + 1) {
                Object[] tArray = new Object[e.getColumn() + 1];
                for (int k = 0; k < tempArray.length; k++) {
                    tArray[k] = tempArray[k];
                }
                Object v;
                try {
                    v = tempRowDataList.get(s.getRow())[s.getColumn()];
                } catch (Exception ex) {
                    v = value;
                    tArray[s.getColumn()] = value;
                }
                tArray[e.getColumn() - i] = v;
                tempRowDataList.set(e.getRow(), tArray);
            } else {
                tempArray[e.getColumn() - i] = tempRowDataList.get(s.getRow())[s.getColumn()];
                tempRowDataList.set(e.getRow(), tempArray);
            }
        }
    }

    protected void createDistinctColumnNames() {
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

    protected abstract void dealWithSomething();

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

    public void process() throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {

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

        private static final int ZERO = 48;
        private static final int ALPHABET = 26;
        private static final int SPECIAL_DATE_TYPE = 31;//poi中没有处理“XXXX年XX月XX日”，这种中文日期，单独提出来处理吧。
        private final DataFormatter formatter;
        private StylesTable stylesTable;
        private ReadOnlySharedStringsTable sharedStringsTable;
        private boolean vIsOpen;
        private boolean fIsOpen;
        private boolean isIsOpen;
        private boolean hfIsOpen;
        private xssfDataType nextDataType;
        private short formatIndex;
        private String formatString;
        private StringBuffer value;
        private StringBuffer formula;
        private StringBuffer headerFooter;
        private boolean formulasNotResults;
        private String cellValue;
        private int thisColumn = -1;
        private int lastColumnNumber = -1;

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
            return "v".equals(name) || ("inlineStr".equals(name) || "t".equals(name) && this.isIsOpen);
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) {
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
                        dealWithCell(attributes);
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

        private void dealWithCell(Attributes attributes) {
            String cellType;
            String cellStyleStr;
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
        }

        public void endElement(String uri, String localName, String name) {
            if (this.isTextTag(name)) {
                isTextTagName();
            } else if ("f".equals(name)) {
                this.fIsOpen = false;
            } else if ("is".equals(name)) {
                this.isIsOpen = false;
            } else if ("row".equals(name)) {
                tempRowDataList.add(tempData.toArray());
                tempData = new ArrayList<String>();
                lastColumnNumber = -1;
            } else if (notEqualSomething(name)) {
                if (equalSomethong(name)) {
                    this.hfIsOpen = false;
                }
            } else {
                this.hfIsOpen = false;
            }
        }

        private void isTextTagName() {
            this.vIsOpen = false;
            switchNextDataType();
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

        private void switchNextDataType() {
            switch (nextDataType) {
                case BOOL:
                    char first = this.value.charAt(0);
                    cellValue = first == ZERO ? "FALSE" : "TRUE";
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
        }

        private boolean notEqualSomething(String name) {
            return !"oddHeader".equals(name) && !"evenHeader".equals(name) && !"firstHeader".equals(name);
        }

        private boolean equalSomethong(String name) {
            return "oddFooter".equals(name) || "evenFooter".equals(name) || "firstFooter".equals(name);
        }

        public void processSSTIndex() {
            String sstIndex = value.toString();
            try {
                int idx = Integer.parseInt(sstIndex);
                XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                cellValue = rtss.toString();
            } catch (NumberFormatException ex) {
                SwiftLoggers.getLogger().error(ex.getMessage(), ex);
            }
        }

        private boolean isBelongInSpecialDateType() {
            return this.formatIndex == SPECIAL_DATE_TYPE;
        }

        public void processNumber() {
            String n = value.toString();
            boolean isDateFormat = this.formatString != null && (HSSFDateUtil.isADateFormat(this.formatIndex, this.formatString) || isBelongInSpecialDateType());
            if (isDateFormat) {
                try {
                    Date date = HSSFDateUtil.getJavaDate(Double.parseDouble(n));
                    cellValue = DateUtils.getDate2LStr(date);
                } catch (Exception e) {
                    cellValue = n;
                }
            } else {
                try {
                    cellValue = NumberToTextConverter.toText(Double.parseDouble(value.toString()));
                } catch (Exception e) {
                    cellValue = n;
                }
            }
        }

        public void characters(char[] ch, int start, int length) {
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
                column = (column + 1) * ALPHABET + c - 'A';
            }
            return column;
        }
    }
}
