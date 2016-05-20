package com.fr.bi.conf.data.source;

import com.fr.base.TableData;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIBasicField;
import com.fr.bi.stable.data.db.*;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.stable.utils.BIServerUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.data.impl.DBTableData;
import com.fr.file.DatasourceManager;
import com.fr.general.data.DataModel;
import com.fr.script.Calculator;
import com.fr.stable.Primitive;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by GUY on 2015/3/3.
 */
public class ServerTableSource extends DBTableSource {

    public static final String XML_TAG = "ServerTableSource";


    /**
     * 获取某个字段的distinct值
     *
     * @param fieldName
     * @param loader
     * @param userId
     */
    @Override
    public Set getFieldDistinctNewestValues(String fieldName, ICubeDataLoader loader, long userId) {
        final HashSet set = new HashSet();
        TableData tableData = getTableData();
        if(tableData instanceof DBTableData){
            BICubeFieldSource field = getFields().get(fieldName);
            if (field == null){
                return set;
            }
            SqlSettedStatement settedStatement = new SqlSettedStatement(((DBTableData) tableData).getDatabase());
            settedStatement.setSql("SELECT distinct " + fieldName + " FROM " + "(" +((DBTableData) tableData).getQuery() + ") " + "t");
            BIDBUtils.runSQL(settedStatement, new BICubeFieldSource[]{field}, new Traversal<BIDataValue>() {
                @Override
                public void actionPerformed(BIDataValue data) {
                    set.add(data.getValue());
                }
            });
            return set;
        } else if (tableData != null) {
            return getDistinctValuesOnlyByTableData(tableData, fieldName);
        }
        return set;
    }

    private HashSet getDistinctValuesOnlyByTableData(TableData tableData, String fieldName) {
        BICubeFieldSource field = getFields().get(fieldName);
        final HashSet set = new HashSet();
        if (field == null){
            return set;
        }
        BIServerUtils.runServer(tableData, new BICubeFieldSource[]{field}, new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                set.add(data.getValue());
            }
        });
        return set;
    }


    /**
     *
     */
    private static final long serialVersionUID = -2942727704344267855L;

    public ServerTableSource() {
        super();
    }

    public ServerTableSource(String dbName, String tableName, String schema, String dbLink) {
        super("__FR__BI__SERVER__", tableName);
    }

    @Override
    public IPersistentTable getPersistentTable() {
        if (dbTable == null) {
            dbTable = BIDBUtils.getServerBITable(tableName);
        }
        return dbTable;
    }

    protected TableData getTableData() {
        return DatasourceManager.getInstance().getTableData(getTableName());
    }

    @Override
	protected TableData createPreviewTableData(){
        return getTableData();
    }

    @Override
    public TableData createTableData(List<String> fields, ICubeDataLoader loader, long userId) throws Exception {
        TableData tableData = getTableData();
        if (fields == null || fields.isEmpty()){
            return tableData;
        }
        DataModel dataModel = null;
        try {
            dataModel = tableData.createDataModel(Calculator.createCalculator());
            if (dataModel.getRowCount() == 0){
                return tableData;
            }
            tableData = BIDBUtils.createTableData(fields, dataModel);
        } catch (Exception e){
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            dataModel.release();
        }
        return tableData;
    }

    @Override
    public int getType() {
        return BIBaseConstant.TABLETYPE.SERVER;
    }

    @Override
    public long read(final Traversal<BIDataValue> travel, BICubeFieldSource[] fields, ICubeDataLoader loader) {

        final long start = System.currentTimeMillis();
        TableData tableData = getTableData();

        if (tableData instanceof DBTableData) {
            return writeDBSimpleIndex(travel, ((DBTableData) tableData).getDatabase(), ((DBTableData) tableData).getQuery(), fields);
        } else {
            final BIBasicField[] columns = fields;
            return BIServerUtils.runServer(tableData, columns, new Traversal<BIDataValue>() {
                @Override
                public void actionPerformed(BIDataValue data) {
                    int i = data.getRow();
                    int j = data.getCol();
                    Object v = data.getValue();
                    Object value = null;
                    switch (columns[j].getFieldType()) {
                        case DBConstant.COLUMN.NUMBER:
                            value = v == null || v == Primitive.NULL ? null : (Number) v;
                            break;
                        case DBConstant.COLUMN.DATE:
                            value = v == null || v == Primitive.NULL ? null : ((Date) v).getTime();
                            break;
                        default:
                            value = v == null || v == Primitive.NULL ? null : v.toString();
                            break;
                    }
                    if (travel != null) {
                        travel.actionPerformed(new BIDataValue(i, j, value));
                    }
                    if (((i + 1) & 0xFFFF) == 0) {// 每执行65536行print一下
                        BIPrintUtils.writeIndexLog("table: " + toString() + CubeConstant.READ_FROM_DB, j + 1, start);
                    }
                }
            });
        }
    }

    private long writeDBSimpleIndex(final Traversal<BIDataValue> travel, final com.fr.data.impl.Connection connect, String query, BICubeFieldSource[] fields) {
        SQLStatement sql = new SQLStatement(connect);
        sql.setFrom( "(" + query + ") " + "t");
        return BIDBUtils.runSQL(sql, fields, new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue v) {
                try {
                    dealWithOneData(travel, v);
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
            }
        });
    }
}