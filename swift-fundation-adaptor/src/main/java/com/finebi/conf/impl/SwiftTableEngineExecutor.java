package com.finebi.conf.impl;

import com.finebi.base.common.resource.FineResourceItem;
import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.session.CommonConfigManager;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.exception.FineExcelFileTypeException;
import com.finebi.conf.exception.FineExcelTableException;
import com.finebi.conf.exception.FineExcelTypeOrQuantityException;
import com.finebi.conf.internalimp.analysis.operator.circulate.CirculateOneFieldOperator;
import com.finebi.conf.internalimp.analysis.operator.circulate.CirculateTwoFieldOperator;
import com.finebi.conf.internalimp.analysis.operator.circulate.FloorItem;
import com.finebi.conf.internalimp.analysis.operator.trans.ColumnRowTransOperator;
import com.finebi.conf.internalimp.analysis.operator.trans.NameText;
import com.finebi.conf.internalimp.basictable.previewdata.FineCirculatePreviewData;
import com.finebi.conf.internalimp.basictable.previewdata.FineColumnTransPreviewData;
import com.finebi.conf.internalimp.basictable.previewdata.FloorPreviewItem;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.internalimp.basictable.table.FineExcelBusinessTable;
import com.finebi.conf.internalimp.service.engine.table.FineTableEngineExecutor;
import com.finebi.conf.provider.SwiftTableManager;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.AbstractFineTable;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.conf.base.EngineComplexConfTable;
import com.finebi.conf.structure.conf.base.EngineConfTable;
import com.finebi.conf.structure.conf.result.EngineConfProduceData;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.dml.Table;
import com.fr.data.impl.Connection;
import com.fr.data.impl.DBTableData;
import com.fr.data.impl.RecursionDataModel;
import com.fr.general.ComparatorUtils;
import com.fr.general.data.DataModel;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftEmptyResult;
import com.fr.swift.adaptor.struct.SwiftSegmentDetailResult;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.provider.DataProvider;
import com.fr.swift.provider.impl.SwiftDataProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.excel.exception.ExcelFileTypeException;
import com.fr.swift.source.excel.exception.ExcelTableHeaderException;
import com.fr.swift.source.excel.exception.ExcelTypeOrQuantityException;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class created on 2018-1-26 14:16:39
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftTableEngineExecutor implements FineTableEngineExecutor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftTableEngineExecutor.class);

    private DataProvider dataProvider;

    @Autowired
    private SwiftTableManager tableManager;

    public SwiftTableEngineExecutor() {
        this.dataProvider = new SwiftDataProvider();
    }

    @Override
    public BIDetailTableResult getPreviewData(FineBusinessTable table, int rowCount) throws Exception {
        try {
            return dataProvider.getDetailPreviewByFields(table, rowCount);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return new SwiftDetailTableResult(new SwiftEmptyResult(), 0, -1);
    }

    @Override
    public BIDetailTableResult getRealData(FineBusinessTable table, int rowCount) {
        return null;
    }


    public BIDetailTableResult getRealData(FineBusinessTable table) throws Exception {
        DataSource dataSource = DataSourceFactory.getDataSourceInCache(table);
        List<Segment> segmentList = dataProvider.getRealData(dataSource);
        return new SwiftSegmentDetailResult(segmentList, dataSource.getMetadata());
    }

//    @Override
//    public BIDetailTableResult getRealData(FineBusinessTable table) throws Exception {
//        DataSource dataSource = DataSourceFactory.getDataSourceInCache(table);
//        List<Segment> segmentList = dataProvider.getRealData(dataSource);
//        return new SwiftSegmentDetailResult(segmentList, dataSource.getMetadata());
//    }

    //todo 正常情况下只有新增表和编辑表会调用这个方法，目前功能代码调用比较混乱，出现了性能问题由功能负责!!!
    @Override
    public List<FineBusinessField> getFieldList(FineBusinessTable table) throws Exception {
        try {
            EntryInfo entryInfo = CommonConfigManager.getEntryInfoSession(getEngineType()).findByName(table.getName());
            Map<String, String> escapeMap = entryInfo != null ? entryInfo.getEscapeMap() : new HashMap<String, String>();

            DataSource dataSource = DataSourceFactory.getDataSourceInSource(table);
            List<FineBusinessField> fieldsList = FieldFactory.transformColumns2Fields(dataSource.getMetadata(), table.getId(), escapeMap);
            return fieldsList;
        } catch (ExcelTypeOrQuantityException e) {
            throw new FineExcelTypeOrQuantityException(e.getMessage());

        } catch (ExcelFileTypeException e) {
            throw new FineExcelFileTypeException(e.getMessage());
        } catch (ExcelTableHeaderException e) {
            throw new FineExcelTableException(e.getMessage());
        } catch (RuntimeException run) {
            LOGGER.error(run.getCause());
            throw (Exception) run.getCause();
        } catch (Exception e) {
            LOGGER.error(e);
            throw e;
        }
    }

    @Override
    public boolean isAvailable(FineResourceItem item) {
        try {
            FineBusinessTable fineBusinessTable = tableManager.getSingleTable(item.getName());
            DataSource dataSource = DataSourceFactory.getDataSourceInCache(fineBusinessTable);
            return dataProvider.isSwiftAvailable(dataSource);
        } catch (FineEngineException e) {
            LOGGER.error(e);
        } catch (Exception ee) {
            LOGGER.error(ee);
        }
        return false;
    }

    @Override
    public String getName(FineResourceItem item) {
        return null;
    }

    @Override
    public boolean refresh(EngineConfTable table) {
        return false;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

    @Override
    public EngineConfProduceData getConfPreResult(FineBusinessTable table) throws Exception {
        FineBusinessTable preTable = ((EngineComplexConfTable) table).getBaseTableBySelected(0);
        List<FineOperator> operators = ((AbstractFineTable) table).getOperators();
        for (int i = 0; i < operators.size(); i++) {
            FineOperator fineOperator = operators.get(i);
            if (fineOperator instanceof ColumnRowTransOperator) {
                ColumnRowTransOperator op = (ColumnRowTransOperator) operators.get(operators.size() - 1);
                return getProduceDataForColumnTrans(op, preTable);
            } else if (fineOperator instanceof CirculateTwoFieldOperator) {
                CirculateTwoFieldOperator op = (CirculateTwoFieldOperator) operators.get(operators.size() - 1);
                return getProduceDataForCirculateTwo(op, preTable, table);
            } else if (fineOperator instanceof CirculateOneFieldOperator) {
                CirculateOneFieldOperator op = (CirculateOneFieldOperator) operators.get(operators.size() - 1);
                return getProduceDataForCirculateOne(op, preTable);
            }
        }
        return null;
    }

    private EngineConfProduceData getProduceDataForColumnTrans(ColumnRowTransOperator op, FineBusinessTable preTable) throws Exception {
        String lcId = op.getLcName();
        int lcIndex = 0;
        List<FineBusinessField> fields = preTable.getFields();
        for (int i = 0; i < fields.size(); i++) {
            if (ComparatorUtils.equals(fields.get(i).getId(), lcId)) {
                lcIndex = i;
                break;
            }
        }
        BIDetailTableResult detailTableResult = getPreviewData(preTable, 100);
        FineColumnTransPreviewData engineConfProduceData = new FineColumnTransPreviewData();
        List<NameText> previewData = new ArrayList<NameText>();
        Set<String> set = new HashSet<String>();
        while (detailTableResult.hasNext()) {
            List<BIDetailCell> dataList = detailTableResult.next();
            if (dataList.get(lcIndex).getData() != null) {
                set.add(dataList.get(lcIndex).getData().toString());
            }
        }
        for (String s : set) {
            previewData.add(new NameText(null, s));
        }
        engineConfProduceData.setPreviewData(previewData);
        return engineConfProduceData;
    }

    private EngineConfProduceData getProduceDataForCirculateTwo(CirculateTwoFieldOperator op, FineBusinessTable preTable, FineBusinessTable table) throws Exception {
        final String tempName = "层级";
        List<String> fieldList = new ArrayList<String>();
        List<FineBusinessField> fields = preTable.getFields();
        fieldList.add(fields.get(findFieldName(fields, op.getIdFieldName())).getName());
        fieldList.add(fields.get(findFieldName(fields, op.getParentIdFieldName())).getName());
        String connectionName = ((FineDBBusinessTable) table).getConnName();
        ConnectionInfo connectionInfo = ConnectionManager.getInstance().getConnectionInfo(connectionName);
        Connection connection = connectionInfo.getFrConnection();
        java.sql.Connection conn = connection.createConnection();
        Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
        Table tb = new Table(connectionInfo.getSchema(), ((FineDBBusinessTable) table).getTableName());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fieldList.size(); i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(dialect.column2SQL(fieldList.get(i)));
        }
        String query = "SELECT " + (fieldList == null || fieldList.isEmpty() ? "*" : sb.toString()) + " FROM " + dialect.table2SQL(tb);
        DBTableData tableData = new DBTableData(connection, query);
        DataModel dm = new RecursionDataModel(tableData.createDataModel(Calculator.createCalculator()), 0, 1);
        int columnSize = dm.getColumnCount();
        int rowSize = dm.getRowCount();
        int k = 1;
        Iterator<FloorItem> iterator = op.getFloors().iterator();
        List<FloorPreviewItem> previewData = new ArrayList<FloorPreviewItem>();
        for (int i = 2; i < columnSize; i++) {
            Set set = new HashSet();
            for (int j = 0; j < rowSize; j++) {
                Object obj = dm.getValueAt(j, i);
                // TODO 各种空值类型
                if (obj == null) {
                    continue;
                }
                set.add(obj);
            }
            List<String> dataList = new ArrayList<String>();
            Iterator iter = set.iterator();
            while (iter.hasNext()) {
                dataList.add(iter.next().toString());
            }
            // TODO length
            if (iterator.hasNext()) {
                FloorItem floorItem = iterator.next();
                previewData.add(new FloorPreviewItem((floorItem.getName()), dataList, 0));
            } else {
                previewData.add(new FloorPreviewItem((tempName + k), dataList, 0));
            }
            k++;
        }
        FineCirculatePreviewData engineConfProduceData = new FineCirculatePreviewData();
        engineConfProduceData.setPreviewData(previewData);
        return engineConfProduceData;
    }

    private static int findFieldName(List<FineBusinessField> fields, String fieldID) {
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < fields.size(); i++) {
            if (ComparatorUtils.equals(fields.get(i).getId(), fieldID)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private EngineConfProduceData getProduceDataForCirculateOne(CirculateOneFieldOperator op, FineBusinessTable preTable) throws Exception {
        String lcId = op.getIdFieldName();
        int divideLength = op.getDivideLength();
        int lcIndex = 0;
        List<FineBusinessField> fields = preTable.getFields();
        for (int i = 0; i < fields.size(); i++) {
            if (ComparatorUtils.equals(fields.get(i).getId(), lcId)) {
                lcIndex = i;
                break;
            }
        }
        BIDetailTableResult detailTableResult = getPreviewData(preTable, 100);
        FineCirculatePreviewData engineConfProduceData = new FineCirculatePreviewData();
        Set<Object> set = new HashSet<Object>();
        while (detailTableResult.hasNext()) {
            List<BIDetailCell> dataList = detailTableResult.next();
            set.add(dataList.get(lcIndex).getData());
        }
        Set lengthSet = getLengthSetFromID(set);
        if (lengthSet.size() > 1) {
            Integer[] len = (Integer[]) lengthSet.toArray(new Integer[lengthSet.size()]);
            int[] lenArray = new int[len.length];
            for (int i = 0; i < len.length; i++) {
                lenArray[i] = len[i];
            }
            engineConfProduceData.setPreviewData(parseDataMode(set, lenArray, false, 0));
            return engineConfProduceData;
        } else if (lengthSet.size() == 1 && StringUtils.isNotEmpty(divideLength + "")) {
            int[] lenArray = createLengthArrayBySameLengthIDUnion(lengthSet, divideLength + "");
            int totalLength = ((Integer) lengthSet.iterator().next()).intValue();
            engineConfProduceData.setPreviewData(parseDataMode(set, lenArray, false, totalLength));
            return engineConfProduceData;
        }
        return engineConfProduceData;
    }

    private List<FloorPreviewItem> parseDataMode(Set ids, int[] len, boolean isTrue, int totalLength) {
        int columnSize = len.length;
        int rowSize = Math.min(100, ids.size());
        final String tempName = "层级";
        List<FloorPreviewItem> previewList = new ArrayList<FloorPreviewItem>();
        for (int i = 0; i < columnSize; i++) {
            List<String> data = new ArrayList<String>();
            Set<String> set = new HashSet<String>();
            Iterator it = ids.iterator();
            int j = 0;
            while (it.hasNext() && j++ < rowSize) {
                Object obj = it.next();
                if (obj == null) {
                    continue;
                }
                String s = obj.toString();
                if (isTrue) {
                    s = dealWithLayerValue(obj.toString(), len, totalLength);
                }
                if (s.length() >= len[i]) {
                    String result = s.substring(0, len[i]);
                    set.add(result);
                }
            }
            Iterator<String> iter = set.iterator();
            while (iter.hasNext()) {
                data.add(iter.next());
            }
            previewList.add(new FloorPreviewItem(tempName + (i + 1), data, len[i]));
        }
        return previewList;
    }

    private String dealWithLayerValue(String v, int[] cz, int totalLength) {
        if (v != null) {
            for (int i = cz.length; i > -1; i--) {
                int len = i == 0 ? 0 : cz[i - 1];
                if (v.length() >= len) {
                    String temp = v.substring(len);
                    if (!isAllCharNone(temp)) {
                        return v.substring(0, i == cz.length ? totalLength : cz[i]);
                    }
                }
            }
        }
        return v;
    }

    private boolean isAllCharNone(String v) {
        if (v != null && v.length() > 0) {
            for (int i = 0, ilen = v.length(); i < ilen; i++) {
                if (v.charAt(i) != '0') {
                    return false;
                }
            }
        }
        return true;
    }

    private int[] createLengthArrayBySameLengthIDUnion(Set lengthSet, String divideLength) {
        int totalLength = ((Integer) lengthSet.iterator().next()).intValue();
        int divide = Integer.valueOf(divideLength).intValue();
        int len = totalLength / divide;
        int tail = totalLength % divide;
        int[] lenArray = new int[len];
        for (int i = 0; i < lenArray.length; i++) {
            lenArray[i] = (i + 1) * divide + tail;
        }
        return lenArray;
    }

    private Set<Integer> getLengthSetFromID(Set set) {
        Set<Integer> intSet = new TreeSet<Integer>();
        for (Object ob : set) {
            if (ob != null) {
                String v = ob.toString();
                int len = v.length();
                Integer key = new Integer(len);
                intSet.add(key);
            }
        }
        return intSet;
    }


    private boolean isFieldCircleExisted(List<FineBusinessField> fields, String idFieldName) {
        // 该字段是否已经自循环列了
        FineBusinessField field = null;
        for (FineBusinessField fieldSource : fields) {
            if (ComparatorUtils.equals(fieldSource.getId(), idFieldName)) {
                field = fieldSource;
            }
        }
        if (null == field) {
            //BILoggerFactory.getLogger().warn("etl union build warning: the source" + source.createJSON() + " has no parent field " + idFieldName);
            return false;
        }
        return false;
    }

    @Override
    public FineBusinessTable createTable(FineBusinessTable table) {
        return null;
    }

    @Override
    public boolean addAdditionalExcel(FineExcelBusinessTable table, String additionalAttachId) {
        return false;
    }
}