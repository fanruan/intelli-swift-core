package com.fr.bi.stable.data.db;

import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.file.BIPictureUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.stable.ColumnRow;
import com.fr.stable.StringUtils;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Young's on 2015/7/3.
 */
public class Excel2003Util implements HSSFListener {
    private static String[] columnNames;
    private static int[] columnTypes;
    private static int columnCount;
    private static List<Object[]> rowDataList;
    private List<Object> currentRowData = new ArrayList<Object>();
    private Map<ColumnRow, ColumnRow> mergeCells = new HashMap<ColumnRow, ColumnRow>();
    private List<String> tempData = new ArrayList<String>();
    private boolean preview = false;
    private POIFSFileSystem fs;
    private int lastRowNumber;
    private boolean outputFormulaValues = true;
    private EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;
    private HSSFWorkbook stubWorkbook;
    private SSTRecord sstRecord;
    private FormatTrackingHSSFListener formatListener;
    private int sheetIndex = -1;
    private int nextRow;
    private int nextColumn;
    private boolean outputNextStringRecord;
    private int thisRow = -1, thisColumn = -1;
    private String thisStr = null;
    public Excel2003Util(String filename) throws IOException {
        this.fs = new POIFSFileSystem(new FileInputStream(filename));
    }
    public Excel2003Util(String filePath, boolean preview) throws Exception {
        preview = preview;
        resetValues();
        Object lock = BIPictureUtils.getImageLock(filePath);
        synchronized (lock) {
            Excel2003Util excel2003Util = new Excel2003Util(filePath);
            excel2003Util.process();
        }
        mergeCells();
        initFieldNames();
        initFieldType();
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
        //此处在外部获取的应当是数据，不包括字段名称
        return  rowDataList.subList(1, rowDataList.size());
    }

    public void resetValues() {
        columnNames = new String[0];
        columnTypes = new int[0];
        columnCount = 0;
        rowDataList = new ArrayList<Object[]>();
    }

    public void process() throws IOException {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);

        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();

        if (outputFormulaValues) {
            request.addListenerForAllRecords(formatListener);
        } else {
            workbookBuildingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(formatListener);
            request.addListenerForAllRecords(workbookBuildingListener);
        }

        factory.processWorkbookEvents(request, fs);
    }

    //获取第一个sheet
    public void processFirstSheet() throws Exception {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);

        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();

        if (outputFormulaValues) {
            request.addListenerForAllRecords(formatListener);
        } else {
            workbookBuildingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(formatListener);
            request.addListenerForAllRecords(workbookBuildingListener);
        }

        factory.processWorkbookEvents(request, fs);
    }

    @Override
    public void processRecord(Record record) {
        initValue();
        boolean isPreView = preview && (nextRow > BIBaseConstant.PREVIEW_COUNT);
        if (sheetIndex > 0 || isPreView) {
            return;
        }
        switchRecordType(record);
        doOtherThing(record);
    }

    public void switchRecordType(Record record) {
        switch (record.getSid()) {
            //begin of file
            case BOFRecord.sid:
                processBOFRecord(record);
                break;
            case RowRecord.sid:
                processRowRecord(record);
                break;
            //collection of label record
            case SSTRecord.sid:
                sstRecord = (SSTRecord) record;
                break;
            case BlankRecord.sid:
                processBlankRecord(record);
                break;
            case BoolErrRecord.sid:
                processBoolRecord(record);
                break;
            case FormulaRecord.sid:
                processFormulaRecord(record);
                break;
            case StringRecord.sid:
                processStringRecord(record);
                break;
            case LabelRecord.sid:
                processLabelRecord(record);
                break;
            case LabelSSTRecord.sid:
                processLabelSSTRecord(record);
                break;
            case NumberRecord.sid:
                processNumberRecord(record);
                break;
            case MergeCellsRecord.sid:
                processMergeRecord(record);
                break;
            default:
                break;
        }
    }

    public void doOtherThing(Record record) {
        if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
            thisRow = mc.getRow();
            thisColumn = mc.getColumn();
            thisStr = StringUtils.EMPTY;
        }
        if (thisStr != null) {
            tempData.add(thisStr);
        }
        if (thisRow > -1) {
            lastRowNumber = thisRow;
        }
        if (record instanceof LastCellOfRowDummyRecord) {
            addOneRow();
        }
    }

    public void processBOFRecord(Record record) {
        BOFRecord br = (BOFRecord) record;
        if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
            if (workbookBuildingListener != null && stubWorkbook == null) {
                stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
            }
            sheetIndex++;
            //只读第一个sheet
            if (sheetIndex > 0) {
                return;
            }
        }
    }

    private void processRowRecord(Record record){
        //此处读到的count都包含了标识行和列结束的cell，即+1了
        columnCount = ((RowRecord)record).getLastCol();
    }

    public void processBlankRecord(Record record) {
        BlankRecord blankRecord = (BlankRecord) record;
        thisRow = blankRecord.getRow();
        thisColumn = blankRecord.getColumn();
        thisStr = StringUtils.EMPTY;
    }

    public void processBoolRecord(Record record) {
        BoolErrRecord boolErrRecord = (BoolErrRecord) record;
        thisRow = boolErrRecord.getRow();
        thisColumn = boolErrRecord.getColumn();
        thisStr = String.valueOf(boolErrRecord.getBooleanValue());
    }

    public void processFormulaRecord(Record record) {
        FormulaRecord formulaRecord = (FormulaRecord) record;
        thisRow = formulaRecord.getRow();
        thisColumn = formulaRecord.getColumn();
        if (outputFormulaValues) {
            if (Double.isNaN(formulaRecord.getValue())) {
                outputNextStringRecord = true;
                nextRow = formulaRecord.getRow();
                nextColumn = formulaRecord.getColumn();
            } else {
                thisStr = formatListener.formatNumberDateCell(formulaRecord);
            }
        } else {
            thisStr = HSSFFormulaParser.toFormulaString(stubWorkbook, formulaRecord.getParsedExpression());
        }
    }

    public void processStringRecord(Record record) {
        if (outputNextStringRecord) {
            StringRecord srec = (StringRecord) record;
            thisStr = srec.getString();
            thisRow = nextRow;
            thisColumn = nextColumn;
            outputNextStringRecord = false;
        }
    }

    public void processLabelRecord(Record record) {
        LabelRecord labelRecord = (LabelRecord) record;
        thisRow = labelRecord.getRow();
        thisColumn = labelRecord.getColumn();
        thisStr = labelRecord.getValue();
    }

    public void processLabelSSTRecord(Record record) {
        LabelSSTRecord labelSSTRecord = (LabelSSTRecord) record;
        thisRow = labelSSTRecord.getRow();
        thisColumn = labelSSTRecord.getColumn();
        if (sstRecord == null) {
            thisStr = StringUtils.EMPTY;
        } else {
            thisStr = sstRecord.getString(labelSSTRecord.getSSTIndex()).toString();
        }
    }

    public void processNumberRecord(Record record) {
        NumberRecord numberRecord = (NumberRecord) record;
        thisRow = numberRecord.getRow();
        thisColumn = numberRecord.getColumn();
        if (formatListener.formatNumberDateCell(numberRecord).contains(",")) {
            thisStr = String.valueOf(numberRecord.getValue());
        } else {
            thisStr = formatListener.formatNumberDateCell(numberRecord);
        }
    }

    public void processMergeRecord(Record record) {
        MergeCellsRecord merge = (MergeCellsRecord) record;
        String m = merge.getAreaAt(0).formatAsString();
        String[] merged = m.split(":");
        String s = merged[0], e = merged[1];
        ColumnRow start = ColumnRow.valueOf(s), end = ColumnRow.valueOf(e);
        mergeCells.put(start, end);
    }

    public void initValue() {
        thisRow = -1;
        thisColumn = -1;
        thisStr = null;
    }

    public void addOneRow() {
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
        rowDataList.add(currentRowData.toArray(new Object[columnCount]));
        currentRowData = new ArrayList<Object>();
        tempData = new ArrayList<String>();
    }

    private void initFieldNames(){
        Object [] firstRow = rowDataList.get(0);
        columnNames = new String[firstRow.length];
        //如果是首行含有空值或特殊字符
        for (int i = 0; i < firstRow.length; i++) {
            //特殊字符替换
            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~\\s]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(firstRow[i].toString());
            columnNames[i] = m.replaceAll(StringUtils.EMPTY).trim();
            if (ComparatorUtils.equals(StringUtils.EMPTY, columnNames[i])) {
                columnNames[i] = Inter.getLocText("BI-Field") + (i + 1);
            }
        }
    }

    //从第二行来读取数据类型
    public void initFieldType() {
        Object [] secondRow = rowDataList.get(1);
        columnTypes = new int[secondRow.length];
        if(secondRow == null){
            for(int i = 0; i < columnNames.length; i++){
                columnTypes[i] = DBConstant.COLUMN.STRING;
            }
            return;
        }
        for (int i = 0; i < secondRow.length; i++) {
            String v = secondRow[i].toString();
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

    //处理合并单元格
    public void mergeCells() {
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
            }
        }
    }
}