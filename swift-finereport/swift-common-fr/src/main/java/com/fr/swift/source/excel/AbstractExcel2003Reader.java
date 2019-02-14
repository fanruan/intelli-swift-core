package com.fr.swift.source.excel;

import com.fr.general.DateUtils;
import com.fr.stable.ColumnRow;
import com.fr.stable.StringUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.third.v2.org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder;
import com.fr.third.v2.org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import com.fr.third.v2.org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import com.fr.third.v2.org.apache.poi.hssf.eventusermodel.HSSFListener;
import com.fr.third.v2.org.apache.poi.hssf.eventusermodel.HSSFRequest;
import com.fr.third.v2.org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import com.fr.third.v2.org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import com.fr.third.v2.org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import com.fr.third.v2.org.apache.poi.hssf.model.HSSFFormulaParser;
import com.fr.third.v2.org.apache.poi.hssf.record.BOFRecord;
import com.fr.third.v2.org.apache.poi.hssf.record.BlankRecord;
import com.fr.third.v2.org.apache.poi.hssf.record.BoolErrRecord;
import com.fr.third.v2.org.apache.poi.hssf.record.FormulaRecord;
import com.fr.third.v2.org.apache.poi.hssf.record.LabelRecord;
import com.fr.third.v2.org.apache.poi.hssf.record.LabelSSTRecord;
import com.fr.third.v2.org.apache.poi.hssf.record.MergeCellsRecord;
import com.fr.third.v2.org.apache.poi.hssf.record.NumberRecord;
import com.fr.third.v2.org.apache.poi.hssf.record.Record;
import com.fr.third.v2.org.apache.poi.hssf.record.RowRecord;
import com.fr.third.v2.org.apache.poi.hssf.record.SSTRecord;
import com.fr.third.v2.org.apache.poi.hssf.record.StringRecord;
import com.fr.third.v2.org.apache.poi.hssf.usermodel.HSSFDateUtil;
import com.fr.third.v2.org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.fr.third.v2.org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fr.swift.source.excel.ExcelConstant.NUMBER_REG;

/**
 * Created by zcf on 2016/11/23.
 */
@Deprecated
public abstract class AbstractExcel2003Reader extends AbstractExcelReader implements HSSFListener {
    private static final int SPECIAL_DATE_TYPE = 31;//poi中没有处理“XXXX年XX月XX日”，这种中文日期，单独提出来处理吧。
    protected String[] columnNames;
    protected List<Object[]> rowDataList;
    private ColumnType[] columnTypes;
    private int columnCount;
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

    public AbstractExcel2003Reader(String filename) throws IOException {
        this.fs = new POIFSFileSystem(new FileInputStream(filename));
    }

    public AbstractExcel2003Reader(String filePath, boolean preview) throws Exception {
        this.preview = preview;
        resetValues();
        Object lock = ExcelFileLockUtils.getImageLock(filePath);
        synchronized (lock) {
            this.fs = new POIFSFileSystem(new FileInputStream(filePath));
            process();
        }
        mergeCells();
        initFieldNames();
        initFieldType();
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
    public Map<ColumnRow, ColumnRow> getMergeInfos() {
        return mergeCells;
    }

    @Override
    public List<Object[]> getRowDataList() {
        //此处在外部获取的应当是数据，不包括字段名称
        return rowDataList.subList(1, rowDataList.size());
    }

    public void resetValues() {
        columnNames = new String[0];
        columnTypes = new ColumnType[0];
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
        boolean isPreView = preview && (nextRow > ExcelConstant.PREVIEW_COUNT);
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

    private void doOtherThing(Record record) {
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

    private void processRowRecord(Record record) {
        //此处读到的count都包含了标识行和列结束的cell，即+1了
        int count = ((RowRecord) record).getLastCol();
        if (count > columnCount) {
            columnCount = count;
        }
    }

    private void processBlankRecord(Record record) {
        BlankRecord blankRecord = (BlankRecord) record;
        thisRow = blankRecord.getRow();
        thisColumn = blankRecord.getColumn();
        thisStr = StringUtils.EMPTY;
    }

    private void processBoolRecord(Record record) {
        BoolErrRecord boolErrRecord = (BoolErrRecord) record;
        thisRow = boolErrRecord.getRow();
        thisColumn = boolErrRecord.getColumn();
        thisStr = String.valueOf(boolErrRecord.getBooleanValue());
    }

    private void processFormulaRecord(Record record) {
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

    private void processStringRecord(Record record) {
        if (outputNextStringRecord) {
            StringRecord srec = (StringRecord) record;
            thisStr = srec.getString();
            thisRow = nextRow;
            thisColumn = nextColumn;
            outputNextStringRecord = false;
        }
    }

    private void processLabelRecord(Record record) {
        LabelRecord labelRecord = (LabelRecord) record;
        thisRow = labelRecord.getRow();
        thisColumn = labelRecord.getColumn();
        thisStr = labelRecord.getValue();
    }

    private void processLabelSSTRecord(Record record) {
        LabelSSTRecord labelSSTRecord = (LabelSSTRecord) record;
        thisRow = labelSSTRecord.getRow();
        thisColumn = labelSSTRecord.getColumn();
        if (sstRecord == null) {
            thisStr = StringUtils.EMPTY;
        } else {
            thisStr = sstRecord.getString(labelSSTRecord.getSSTIndex()).toString();
        }
    }

    private boolean isBelongInSpecialDateType(NumberRecord numberRecord) {
        return formatListener.getFormatIndex(numberRecord) == SPECIAL_DATE_TYPE;
    }

    private boolean isDateType(NumberRecord numberRecord) {
        return HSSFDateUtil.isADateFormat(formatListener.getFormatIndex(numberRecord), formatListener.getFormatString(numberRecord));
    }

    private void processNumberRecord(Record record) {
        NumberRecord numberRecord = (NumberRecord) record;
        thisRow = numberRecord.getRow();
        thisColumn = numberRecord.getColumn();
        if (formatListener.formatNumberDateCell(numberRecord).contains(",")) {
            thisStr = String.valueOf(numberRecord.getValue());
        } else if (formatListener.formatNumberDateCell(numberRecord).contains("%")) {
            thisStr = String.valueOf(numberRecord.getValue());
        } else if (isDateType(numberRecord) || isBelongInSpecialDateType(numberRecord)) {
            Date date = HSSFDateUtil.getJavaDate(numberRecord.getValue());
            thisStr = DateUtils.getDate2LStr(date);
        } else {
            thisStr = formatListener.formatNumberDateCell(numberRecord);
        }
    }

    private void processMergeRecord(Record record) {
        try {
            MergeCellsRecord merge = (MergeCellsRecord) record;
            Short num = merge.getNumAreas();
            for (int i = 0; i < num; i++) {
                String m = merge.getAreaAt(i).formatAsString();
                String[] merged = m.split(":");
                String s = merged[0], e = merged[1];
                ColumnRow start = ColumnRow.valueOf(s), end = ColumnRow.valueOf(e);
                mergeCells.put(start, end);
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        }
    }

    private void initValue() {
        thisRow = -1;
        thisColumn = -1;
        thisStr = null;
    }

    private void addOneRow() {
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

    protected abstract void initFieldNames();

    //从第二行来读取数据类型
    private void initFieldType() {
        Object[] secondRow = rowDataList.get(1);
        columnTypes = new ColumnType[secondRow.length];
        if (secondRow == null) {
            for (int i = 0; i < columnNames.length; i++) {
                columnTypes[i] = ColumnType.STRING;
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
            if (v.matches(NUMBER_REG)) {
                columnTypes[i] = ColumnType.NUMBER;
            } else if (dateType) {
                columnTypes[i] = ColumnType.DATE;
            } else {
                columnTypes[i] = ColumnType.STRING;
            }
        }
    }

    //处理合并单元格
    private void mergeCells() {
        Map<ColumnRow, ColumnRow> merges = mergeCells;
        for (Map.Entry<ColumnRow, ColumnRow> m : merges.entrySet()) {
            ColumnRow s = m.getKey();
            ColumnRow e = m.getValue();
            if (s.getRow() == e.getRow()) {
                //如果是横向合并
                rowMerge(s, e);
            } else if (s.getColumn() == e.getColumn()) {
                //纵向合并
                colMerge(s, e);
            } else {
                //横纵都合并
                int mergedRowCount = e.getRow() - s.getRow();
                Object v = rowDataList.get(s.getRow())[s.getColumn()];
                for (int i = 0; i <= mergedRowCount; i++) {
                    ColumnRow start = ColumnRow.valueOf(s.getColumn(), s.getRow() + i);
                    ColumnRow end = ColumnRow.valueOf(e.getColumn(), s.getRow() + i);
                    rowColMerge(start, end, v);
                }
            }
        }
    }

    private void rowMerge(ColumnRow s, ColumnRow e) {
        int mergedColCount = e.getColumn() - s.getColumn();
        Object[] tempArray = rowDataList.get(s.getRow());
        Object v = rowDataList.get(s.getRow())[s.getColumn()];
        for (int i = 0; i < mergedColCount; i++) {
            tempArray[e.getColumn() - i] = v;
        }
        rowDataList.set(s.getRow(), tempArray);
    }

    private void colMerge(ColumnRow s, ColumnRow e) {
        int mergedRowCount = e.getRow() - s.getRow();
        Object v = rowDataList.get(s.getRow())[s.getColumn()];
        for (int j = 0; j < mergedRowCount; j++) {
            Object[] tempArray = rowDataList.get(e.getRow() - j);
            tempArray[e.getColumn()] = v;
            rowDataList.set(e.getRow() - j, tempArray);
        }
    }

    private void rowColMerge(ColumnRow s, ColumnRow e, Object v) {
        int mergedColCount = e.getColumn() - s.getColumn();
        Object[] tempArray = rowDataList.get(s.getRow());
        for (int i = 0; i <= mergedColCount; i++) {
            tempArray[e.getColumn() - i] = v;
        }
        rowDataList.set(s.getRow(), tempArray);
    }
}
