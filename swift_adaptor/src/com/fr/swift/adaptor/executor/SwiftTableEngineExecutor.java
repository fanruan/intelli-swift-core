package com.fr.swift.adaptor.executor;

import com.finebi.base.common.resource.FineResourceItem;
import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.analysis.operator.circulate.CirculateOneFieldOperator;
import com.finebi.conf.internalimp.analysis.operator.circulate.CirculateTwoFieldOperator;
import com.finebi.conf.internalimp.analysis.operator.trans.ColumnRowTransOperator;
import com.finebi.conf.internalimp.analysis.operator.trans.NameText;
import com.finebi.conf.internalimp.basictable.previewdata.FIneCirculatePreviewData;
import com.finebi.conf.internalimp.basictable.previewdata.FIneColumnTransPreviewData;
import com.finebi.conf.internalimp.basictable.previewdata.FloorPreviewItem;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.internalimp.service.engine.table.FineTableEngineExecutor;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.AbstractFineTable;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.conf.base.EngineComplexConfTable;
import com.finebi.conf.structure.conf.base.EngineConfTable;
import com.finebi.conf.structure.conf.result.EngineConfProduceData;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.finebi.conf.utils.FineConnectionUtils;
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
import com.fr.swift.adaptor.preview.SwiftDataPreviewer;
import com.fr.swift.adaptor.struct.SwiftDetailCell;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftEmptyResult;
import com.fr.swift.adaptor.struct.SwiftRealDetailResult;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;

import java.util.*;

/**
 * This class created on 2018-1-26 14:16:39
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftTableEngineExecutor implements FineTableEngineExecutor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftTableEngineExecutor.class);

    @Override
    public BIDetailTableResult getPreviewData(FineBusinessTable table, int rowCount) throws Exception {
        DataSource dataSource = IndexingDataSourceFactory.transformDataSource(table);
        if (dataSource != null) {
            SwiftSourceTransfer transfer = SwiftDataPreviewer.createPreviewTransfer(dataSource, rowCount);
            SwiftResultSet swiftResultSet = transfer.createResultSet();
            BIDetailTableResult detailTableResult = new SwiftDetailTableResult(swiftResultSet);
            return detailTableResult;
        }
        return new SwiftDetailTableResult(new SwiftEmptyResult());
    }

    @Override
    public BIDetailTableResult getRealData(FineBusinessTable table) throws Exception {
        DataSource dataSource = IndexingDataSourceFactory.transformDataSource(table);
        List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        List<List<BIDetailCell>> dataList = new ArrayList<List<BIDetailCell>>();
        for (Segment segment : segments) {
            List<DetailColumn> columnList = new ArrayList<DetailColumn>();
            int count = segment.getRowCount();
            for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
                String columnName = swiftMetaData.getColumnName(i);
                ColumnKey columnKey = new ColumnKey(columnName);
                columnList.add(segment.getColumn(columnKey).getDetailColumn());
            }
            for (int i = 0; i < count; i++) {
                List<BIDetailCell> cellList = new ArrayList<BIDetailCell>();
                for (int j = 0; j < swiftMetaData.getColumnCount(); j++) {
                    BIDetailCell cell = new SwiftDetailCell(columnList.get(j).get(i));
                    cellList.add(cell);
                }
                dataList.add(cellList);
            }
        }
        BIDetailTableResult realDetailResult = new SwiftRealDetailResult(dataList.iterator(), dataList.size(), swiftMetaData.getColumnCount());
        return realDetailResult;
    }

    @Override
    public List<FineBusinessField> getFieldList(FineBusinessTable table) throws Exception {
        DataSource dataSource = IndexingDataSourceFactory.transformDataSource(table);
        if (dataSource == null) {
            return new ArrayList<FineBusinessField>();
        }
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        return FieldFactory.transformColumns2Fields(swiftMetaData);
    }

    @Override
    public boolean isAvailable(FineResourceItem item) {
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
        //DataSource dataSource = IndexingDataSourceFactory.transformDataSource(table);
        FineBusinessTable preTable = ((EngineComplexConfTable)table).getBaseTableBySelected(0);
        List<FineOperator> operators = ((AbstractFineTable)table).getOperators();
        for(int i = 0; i < operators.size(); i++) {
            FineOperator fineOperator = operators.get(i);
            if(fineOperator instanceof ColumnRowTransOperator) {
                ColumnRowTransOperator op = (ColumnRowTransOperator) operators.get(operators.size() - 1);
                return getProduceDataForColumnTrans(op, preTable);
            } else if(fineOperator instanceof CirculateTwoFieldOperator) {
                CirculateTwoFieldOperator op = (CirculateTwoFieldOperator) operators.get(operators.size() - 1);
                return getProduceDataForCirculateTwo(op, preTable, table);
            } else if(fineOperator instanceof CirculateOneFieldOperator) {
                CirculateOneFieldOperator op = (CirculateOneFieldOperator) operators.get(operators.size() - 1);
                return getProduceDataForCirculateOne(op, preTable);
            }
        }
        return null;
    }

    private EngineConfProduceData getProduceDataForColumnTrans(ColumnRowTransOperator op, FineBusinessTable preTable) throws Exception{
        String lcId = op.getLcName();
        int lcIndex = 0;
        List<FineBusinessField> fields = preTable.getFields();
        for (int i = 0; i < fields.size(); i++){
            if (ComparatorUtils.equals(fields.get(i).getId(), lcId)){
                lcIndex = i;
                break;
            }
        }
        BIDetailTableResult detailTableResult = getPreviewData(preTable, 100);
        FIneColumnTransPreviewData engineConfProduceData = new FIneColumnTransPreviewData();
        List<NameText> previewData = new ArrayList<NameText>();
        Set<String> set = new HashSet<String>();
        while (detailTableResult.hasNext()) {
            List<BIDetailCell> dataList = detailTableResult.next();
            if (dataList.get(lcIndex).getData() != null){
                set.add(dataList.get(lcIndex).getData().toString());
            }
        }
        for (String s: set){
            previewData.add(new NameText(null, s));
        }
        engineConfProduceData.setPreviewData(previewData);
        return engineConfProduceData;
    }

    private void dealWithID(final int cl, final int i, final List list,
                            final Segment segment, final ColumnKey idCIndex,
                            final ColumnKey pidCIndex, final Segment[] segments) {

        DictionaryEncodedColumn get1 = segment.getColumn(idCIndex).getDictionaryEncodedColumn();
        Object id = get1.getValue(get1.getIndexByRow(i));
        if(id != null && list.size() < cl) {
            list.add(id);
            DictionaryEncodedColumn get2 = segment.getColumn(pidCIndex).getDictionaryEncodedColumn();
            Object pid = get2.getValue(get2.getIndexByRow(i));
            if(pid != null) {
                for(int k = 0; k < segments.length; k++) {
                    DictionaryEncodedColumn gts = segments[k].getColumn(idCIndex).getDictionaryEncodedColumn();
                    int index = gts.getIndex(pid);
                    ImmutableBitMap bitMap = segments[k].getColumn(idCIndex).getBitmapIndex().getBitMapIndex(index);
                    final int indexOfSeg = k;
                    if(bitMap != null) {
                        bitMap.breakableTraversal(new BreakTraversalAction() {
                            @Override
                            public boolean actionPerformed(int row) {
                                dealWithID(cl, row, list, segments[indexOfSeg], idCIndex, pidCIndex, segments);
                                return true;
                            }
                        });
                    }
                }
            }
        }
    }

    private EngineConfProduceData getProduceDataForCirculateTwo(CirculateTwoFieldOperator op, FineBusinessTable preTable, FineBusinessTable table) throws Exception{
        final String tempName = "层级";
        List<String> fieldList = new ArrayList<String>();
        List<FineBusinessField> fields = preTable.getFields();
        fieldList.add(fields.get(findFieldName(fields, op.getIdFieldName())).getName());
        fieldList.add(fields.get(findFieldName(fields, op.getParentIdFieldName())).getName());
        String connectionName = ((FineDBBusinessTable)table).getConnName();
        FineConnection fineConnection = FineConnectionUtils.getConnectionByName(connectionName);
        Connection connection = fineConnection.getConnection();
        java.sql.Connection conn = connection.createConnection();
        Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
        Table tb = new Table(fineConnection.getSchema(), ((FineDBBusinessTable) table).getTableName());
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
            previewData.add(new FloorPreviewItem((tempName + k), dataList, 0));
        }
        FIneCirculatePreviewData engineConfProduceData = new FIneCirculatePreviewData();
        engineConfProduceData.setPreviewData(previewData);
        return engineConfProduceData;
    }

    private static int findFieldName(List<FineBusinessField> fields, String fieldID) {
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < fields.size(); i++){
            if (ComparatorUtils.equals(fields.get(i).getId(), fieldID)){
                index = i;
                break;
            }
        }
        return index;
    }

    private EngineConfProduceData getProduceDataForCirculateOne(CirculateOneFieldOperator op, FineBusinessTable preTable) throws Exception{
        String lcId = op.getIdFieldName();
        int divideLength = op.getDivideLength();
        int lcIndex = 0;
        List<FineBusinessField> fields = preTable.getFields();
        for (int i = 0; i < fields.size(); i++){
            if (ComparatorUtils.equals(fields.get(i).getId(), lcId)){
                lcIndex = i;
                break;
            }
        }
        BIDetailTableResult detailTableResult = getPreviewData(preTable, 100);
        FIneCirculatePreviewData engineConfProduceData = new FIneCirculatePreviewData();
        Set<Object> set = new HashSet<Object>();
        while (detailTableResult.hasNext()) {
            List<BIDetailCell> dataList = detailTableResult.next();
            set.add(dataList.get(lcIndex).getData().toString());
        }
        Set lengthSet = getLengthSetFromID(set);
        if (lengthSet.size() > 1) {
            Integer[] len = (Integer[]) lengthSet.toArray(new Integer[lengthSet.size()]);
            int[] lenArray = new int[len.length];
            for (int i = 0; i < len.length; i++) {
                lenArray[i] = len[i].intValue();
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

    private List<FloorPreviewItem> parseDataMode(Set ids, int[] len, boolean isTrue, int totalLength)  {
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
                if(isTrue) {
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
                if (!intSet.contains(key)) {
                    intSet.add(key);
                }
            }
        }
        return intSet;
    }


    // 该字段是否已经自循环列了
    private boolean isFieldCircleExisted(List<FineBusinessField> fields, String idFieldName) {
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
}
