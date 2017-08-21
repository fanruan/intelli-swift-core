package com.finebi.cube.gen.oper;

import com.finebi.cube.adapter.BIUserCubeManager;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BICubeLogExceptionInfo;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.SQLTableSource;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.data.DBQueryExecutor;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.SqlSettedStatement;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.bi.util.BICubeDBUtils;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.dml.Table;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.stable.collections.array.IntArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by kary on 16/7/13.
 */
public class BISourceDataPartTransport extends BISourceDataTransport {
    private static final Logger LOGGER = LoggerFactory.getLogger(BISourceDataPartTransport.class);
    private static String ADD = "add";
    private static String DELETE = "delete";
    private static String MODIFY = "modify";

    protected UpdateSettingSource tableUpdateSetting;

    public BISourceDataPartTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version, UpdateSettingSource tableUpdateSetting) {
        super(cube, tableSource, allSources, parentTableSource, version);
        this.tableUpdateSetting = tableUpdateSetting;
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        BILogManagerProvider biLogManager = BIConfigureManagerCenter.getLogManager();
        LOGGER.info(BIStringUtils.append("The table:", fetchTableInfo(), " start transport task",
                BILogHelper.logCubeLogTableSourceInfo(tableSource.getSourceID())));
        BILogHelper.cacheCubeLogTableNormalInfo(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_START, System.currentTimeMillis());
        long t = System.currentTimeMillis();
        try {
            LOGGER.info(BIStringUtils.append("The table:", fetchTableInfo(), " copy old FineIndex files"));
            copyFromOldCubes();
            tableEntityService.recordCurrentExecuteTime();
            LOGGER.info(BIStringUtils.append("The table:", fetchTableInfo(), " record table structure info"));
            recordTableInfo();
            long count = transport();
            LOGGER.info(BIStringUtils.append("The table:", fetchTableInfo(), " finish transportation operation and record ",
                    String.valueOf(count), " records"));
            ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
            ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(cube.getCubeResourceRetrievalService().getCubeConfiguration());
            cube = new BICube(resourceRetrievalService, discovery);
            tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
            if (count >= 0) {
                tableEntityService.recordRowCount(count);
            }
            tableEntityService.addVersion(version);
            tableEntityService.forceReleaseWriter();
            tableEntityService.clear();
            long tableCostTime = System.currentTimeMillis() - t;
            LOGGER.info("transport cost time: " + tableCostTime + BILogHelper.logCubeLogTableSourceInfo(tableSource.getSourceID()));
            BILogHelper.cacheCubeLogTableNormalInfo(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END, System.currentTimeMillis());
            try {
                biLogManager.infoTable(tableSource.getPersistentTable(), tableCostTime, UserControl.getInstance().getSuperManagerID());
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
            return null;
        } catch (Throwable e) {
            try {
                biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
            } catch (Exception e1) {
                BILoggerFactory.getLogger().error(e1.getMessage(), e1);
            }
            BILogHelper.cacheCubeLogTableNormalInfo(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END, System.currentTimeMillis());
            BICubeLogExceptionInfo exceptionInfo = new BICubeLogExceptionInfo(System.currentTimeMillis(), "Transport Exception", e.getMessage(), e, tableSource.getSourceID());
            BILogHelper.cacheCubeLogTableException(tableSource.getSourceID(), exceptionInfo);
            BILoggerFactory.getLogger(BISourceDataPartTransport.class).error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private long transport() {
        List<ICubeFieldSource> fieldList = tableEntityService.getFieldInfo();
        ICubeFieldSource[] cubeFieldSources = new ICubeFieldSource[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++) {
            fieldList.get(i).setTableBelongTo(tableSource);
            cubeFieldSources[i] = fieldList.get(i);
        }
        long rowCount = tableEntityService.isVersionAvailable() ? tableEntityService.getRowCount() : 0;
        TreeSet<Integer> sortRemovedList = new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
        if (tableEntityService.isRemovedListAvailable()) {
            IntArray removedList = tableEntityService.getRemovedList();
            for (int i = 0; i < removedList.size; i++) {
                sortRemovedList.add(Integer.valueOf(removedList.get(i)));
            }
        }
        BIUserCubeManager loader = new BIUserCubeManager(UserControl.getInstance().getSuperManagerID(), cube);
        Map<String, List<Object[]>> resultMap = preHandleSQLs(cubeFieldSources, addDateCondition(tableUpdateSetting.getPartDeleteSQL()),
                addDateCondition(tableUpdateSetting.getPartAddSQL()),
                addDateCondition(tableUpdateSetting.getPartModifySQL()));
          /*remove*/
        if (isLegalSQL(addDateCondition(tableUpdateSetting.getPartDeleteSQL()))) {
            String columnName = getKeyName(addDateCondition(tableUpdateSetting.getPartDeleteSQL()));
            if (getCubeFieldSource(cubeFieldSources, columnName) != null) {
                sortRemovedList = dealWithRemove(columnName, resultMap.get(DELETE), sortRemovedList, loader);
            } else {
                BILoggerFactory.getLogger().error("can not find field " + columnName);
            }

        }
        /*add*/
        if (isLegalSQL(addDateCondition(tableUpdateSetting.getPartAddSQL()))) {
            rowCount = dealWidthAdd(resultMap.get(ADD), rowCount);
            tableEntityService.forceReleaseWriter();
        }
        /*modify*/
        if (isLegalSQL(addDateCondition(tableUpdateSetting.getPartModifySQL()))) {
            String columnName = getKeyName(addDateCondition(tableUpdateSetting.getPartModifySQL()));
            sortRemovedList = dealWithRemove(columnName, resultMap.get(MODIFY), sortRemovedList, loader);
            rowCount = dealWidthAdd(resultMap.get(MODIFY), rowCount);
            tableEntityService.forceReleaseWriter();
        }

        if (null != sortRemovedList && sortRemovedList.size() != 0) {
            tableEntityService.recordRemovedLine(sortRemovedList);
            tableEntityService.forceReleaseWriter();
        }
        return rowCount;
    }

    private long dealWidthAdd(List<Object[]> addList, long rowCount) {
        tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
        try {
            int row = (int) rowCount;
            for (int i = 1; i < addList.size(); i++) {
                Object[] objects = addList.get(i);
                if (objects != null) {
                    for (int column = 0; column < objects.length; column++) {
                        List<ICubeFieldSource> fieldList = tableEntityService.getFieldInfo();
                        BIDataValue biDataValue = new BIDataValue(row, column, objects[column]);
                        if (objects[column] == null && fieldList.get(column).getFieldType() == DBConstant.COLUMN.STRING) {
                            biDataValue = new BIDataValue(row, column, "");
                        }
                        tableEntityService.increaseAddDataValue(biDataValue);
                    }
                    row++;
                }
            }
            return row;
        } catch (BICubeColumnAbsentException e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private TreeSet<Integer> dealWithRemove(String columnName, List<Object[]> deleteLists, final TreeSet<Integer> sortRemovedList, ICubeDataLoader loader) {
        BIKey key = new IndexKey(columnName);
        ICubeTableService oldTi = loader.getTableIndex(tableSource);
        final ICubeColumnIndexReader getter = oldTi.loadGroup(key);

        int columnIndex = 0;
        for (Object object : deleteLists.get(0)) {
            if (ComparatorUtils.equals(columnName, object)) {
                break;
            }
            columnIndex++;
        }

        for (int i = 1; i < deleteLists.size(); i++) {
            Object[] objects = deleteLists.get(i);
            if (objects != null) {
                getter.getIndex(objects[columnIndex]).Traversal(new SingleRowTraversalAction() {
                    @Override
                    public void actionPerformed(int data) {
                        sortRemovedList.add(data);
                    }
                });
            }
        }
        return sortRemovedList;
    }

    private String addDateCondition(String sql) {
        //替换上次更新时间
        if (tableEntityService.isLastExecuteTimeAvailable() && null != tableEntityService.getLastExecuteTime()) {
            Date lastTime = tableEntityService.getLastExecuteTime();
            Pattern lastTimePat = Pattern.compile("\\$[\\{]" + DBConstant.LAST_UPDATE_TIME + "[\\}]");
            sql = replacePattern(sql, lastTimePat, lastTime);
        }

        //替换当前更新时间
        Date currentTime = tableEntityService.getCurrentExecuteTime();
        Pattern currentTimePat = Pattern.compile("\\$[\\{]" + DBConstant.CURRENT_UPDATE_TIME + "[\\}]");
        sql = replacePattern(sql, currentTimePat, currentTime);
        tableEntityService.clear();
        return sql;
    }

    private String replacePattern(String sql, Pattern pattern, Date date) {
        Matcher matcher = pattern.matcher(sql);
        String dateStr = DateUtils.DATETIMEFORMAT2.format(date);
        while (matcher.find()) {
            String matchStr = matcher.group(0);
            sql = sql.replace(matchStr, dateStr);
        }
        return sql;
    }

    /**
     * 以前SQL数据集和数据表的SQL语句使用两套逻辑处理
     * 统一逻辑，select id和 select id,a1,a2,a3都是合理的
     *
     * @param fields
     * @param sql
     * @return
     * @throws Exception
     */
    private String getModifySql(ICubeFieldSource[] fields, String sql) {
        sql = addDateCondition(sql);
        com.fr.data.impl.Connection connection = null;
        java.sql.Connection sqlConn = null;
        String finalSql = null;
        try {
            if (tableSource.getType() == BIBaseConstant.TABLETYPE.DB) {
                connection = ((DBTableSource) tableSource).getConnection();
            }
            if (tableSource.getType() == BIBaseConstant.TABLETYPE.SQL) {
                connection = ((SQLTableSource) tableSource).getConnection();
            }
            SqlSettedStatement sqlStatement = new SqlSettedStatement(connection);
            sqlStatement.setSql(sql);
            sqlConn = sqlStatement.getSqlConn();
            Dialect dialect = DialectFactory.generateDialect(sqlConn, connection.getDriver());

            int columnNum = BICubeDBUtils.getColumnNum(connection, sqlStatement, sql);
            if (columnNum == fields.length) {
                finalSql = sql;
            }
            if (columnNum == 1) {
                String columnName = BICubeDBUtils.getFirstColumnName(connection, sqlStatement, sql);
                ICubeFieldSource f = getCubeFieldSource(fields, columnName);
                if (f == null) {
                    return null;
                }
                if (tableSource.getType() == BIBaseConstant.TABLETYPE.DB) {
                    String dbName = ((DBTableSource) tableSource).getDbName();
                    Table table = new Table(BIConnectionManager.getBIConnectionManager().getSchema(dbName), tableSource.getTableName());
                    finalSql = "SELECT *" + " FROM " + dialect.table2SQL(table) + " WHERE " + columnName + " IN " + "(" + sql + ")";
                }
                if (tableSource.getType() == BIBaseConstant.TABLETYPE.SQL) {
                    finalSql = ((SQLTableSource) tableSource).getQuery() + " WHERE " + columnName + " IN " + "(" + sql + ")";
                }
            } else {
                LOGGER.error("SQL syntax error: " + tableSource.getTableName() + " columns length incorrect " + sql);
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        } finally {
            DBUtils.closeConnection(sqlConn);
        }
        return finalSql;
    }


    private boolean isLegalSQL(String sql) {
        BINonValueUtils.checkNull(sql);
        LOGGER.info(BIStringUtils.append(BILogHelper.logTableSource(tableSource, " "), " check the sql", sql));
        if (BIStringUtils.isEmptyString(sql) || BIStringUtils.isBlankString(sql)) {
            LOGGER.info(BIStringUtils.append(BILogHelper.logTableSource(tableSource, " "), " the sql is blank"));
            return false;
        } else if (!containSelect(sql)) {
            LOGGER.info(BIStringUtils.append(BILogHelper.logTableSource(tableSource, " "), " the sql should be used to query and must contain keyword select "));
            return false;
        }
        return true;
    }

    private boolean containSelect(String sql) {
        BINonValueUtils.checkNull(sql);
        return sql.toUpperCase().contains("SELECT");
    }

    private Map<String, List<Object[]>> preHandleSQLs(ICubeFieldSource[] fields, String partDeleteSQL, String partAddSQL, String partModifySQL) {
        List<Object[]> addList = new ArrayList<Object[]>();
        List<Object[]> deleteList = new ArrayList<Object[]>();
        List<Object[]> modifyList = new ArrayList<Object[]>();
        /**
         * 添加删除SQL或者修改SQL为空的情况。
         */
        if (isLegalSQL(partAddSQL)) {
            LOGGER.info("The table: " + BILogHelper.logTableSource(tableSource, " ") + "execute sql:#" + partAddSQL + "# to add data");
            addList = executeSQL(fields, partAddSQL);
        } else {
            LOGGER.warn("The table: " + BILogHelper.logTableSource(tableSource, " ") + ", it's add sql is empty");
        }
        if (isLegalSQL(partDeleteSQL)) {
            LOGGER.info("The table: " + BILogHelper.logTableSource(tableSource, " ") + "execute sql:#" + partAddSQL + "# to delete data");
            deleteList = executeSQL(new ICubeFieldSource[]{getCubeFieldSource(fields, getKeyName(partDeleteSQL))}, partDeleteSQL);
        } else {
            LOGGER.warn("The table: " + BILogHelper.logTableSource(tableSource, " ") + ", it's delete sql is empty");
        }
        if (isLegalSQL(partModifySQL)) {
            LOGGER.info("The table: " + BILogHelper.logTableSource(tableSource, " ") + "execute sql:#" + partAddSQL + "# to update data");

            modifyList = executeSQL(fields, getModifySql(fields, partModifySQL));
        } else {
            LOGGER.warn("The table: " + BILogHelper.logTableSource(tableSource, " ") + ", it's modify sql is empty");

        }
   /*
        * 预处理逻辑：对于同一条Key的记录
        * 1. 新增中出现n次，删除中出现n-1次，则处理后新增留一次，删除中没有该记录
        * 2. 修改中出现n次，处理后则留一次
        *
        * 经过1处理后，某个key的记录最多在新增中出现一次或在删除中出现一次
        * 经过2处理后，某个key的记录最多在修改中出现一次
        *
        * 3. 如果修改和删除中都出现某条记录，则留删除，修改中去掉该记录
        * 4. 如果修改和新增中都出现某条记录，则留新增，修改中去掉该记录
        *
        * */
        handleAddAndDelete(addList, deleteList);
        handleModify(modifyList);
        handleModifyAndDelete(modifyList, deleteList);
        handleModifyAndAdd(modifyList, addList);
        Map<String, List<Object[]>> resultMap = new HashMap<String, List<Object[]>>();
        resultMap.put(ADD, addList);
        resultMap.put(MODIFY, modifyList);
        resultMap.put(DELETE, deleteList);
        return resultMap;
    }

    private List<Object[]> executeSQL(final ICubeFieldSource[] fields, String SQL) {
        final List<Object[]> rows = new ArrayList<Object[]>();
        Object[] objects = new Object[fields.length];
        rows.add(objects);
        for (int i = 0; i < fields.length; i++) {
            objects[i] = fields[i].getFieldName();
        }

        SqlSettedStatement sqlStatement = new SqlSettedStatement(((DBTableSource) tableSource).getConnection());
        sqlStatement.setSql(addDateCondition(SQL));
        Traversal<BIDataValue> removeTraversal = new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                if (rows.size() - 1 < data.getRow() + 1) {
                    Object[] objects = new Object[fields.length];
                    rows.add(objects);
                    objects[data.getCol()] = data.getValue();
                } else {
                    rows.get(data.getRow() + 1)[data.getCol()] = data.getValue();
                }
            }
        };
        DBQueryExecutor.getInstance().runSQL(sqlStatement, fields, removeTraversal);
        return rows;
    }

    private void handleAddAndDelete(List<Object[]> addList, List<Object[]> deleteList) {
        if (addList.size() > 1 && deleteList.size() > 1) {
            int columnIndex = 0;
            for (int j = 0; j < addList.get(0).length; j++) {
                if (ComparatorUtils.equals(addList.get(0)[j], deleteList.get(0)[0])) {
                    columnIndex = j;
                    break;
                }
            }
            for (int i = 1; i < addList.size(); i++) {
                if (addList.get(i) != null && removeValueFromList(deleteList, addList.get(i)[columnIndex], (String) addList.get(0)[columnIndex])) {
                    addList.set(i, null);
                }
            }
        }
    }

    private void handleModify(List<Object[]> modifyList) {
        if (modifyList.size() > 1) {
            for (int i = 1; i < modifyList.size(); i++) {
                Object[] objects = modifyList.get(i);
                if (objects != null) {
                    removeDuplicateValue(modifyList, objects[0], i + 1);
                }
            }
        }
    }

    private void handleModifyAndDelete(List<Object[]> modifyList, List<Object[]> deleteList) {
        removeFromModify(modifyList, deleteList);
    }

    private void handleModifyAndAdd(List<Object[]> modifyList, List<Object[]> addList) {
        removeFromModify(modifyList, addList);
    }

    private void removeFromModify(List<Object[]> modifyList, List<Object[]> list) {
        if (list.size() > 1 && modifyList.size() > 1) {
            int columnIndex = 0;
            for (int j = 0; j < list.get(0).length; j++) {
                if (ComparatorUtils.equals(list.get(0)[j], modifyList.get(0)[0])) {
                    columnIndex = j;
                    break;
                }
            }

            for (int i = 1; i < list.size(); i++) {
                if (list.get(i) != null) {
                    removeValueFromList(modifyList, list.get(i)[columnIndex], (String) list.get(0)[columnIndex]);
                }
            }
        }
    }

    private boolean removeValueFromList(List<Object[]> list, Object value, String columnName) {
        int column = -1;
        for (int i = 0; i < list.get(0).length; i++) {
            if (ComparatorUtils.equals(list.get(0)[i], columnName)) {
                column = i;
            }
        }

        if (column > -1) {
            for (int i = 1; i < list.size(); i++) {
                if (list.get(i) != null && ComparatorUtils.equals(list.get(i)[column], value)) {
                    list.set(i, null);
                    return true;
                }
            }
        }
        return false;
    }

    private void removeDuplicateValue(List<Object[]> list, Object value, int row) {
        for (int i = row; i < list.size(); i++) {
            if (list.get(i) != null && ComparatorUtils.equals(list.get(i)[0], value)) {
                list.set(i, null);
            }
        }
    }

    private String getKeyName(String sql) {
        String name = null;
        java.sql.Connection sqlConn = null;
        try {
            com.fr.data.impl.Connection connection = ((DBTableSource) tableSource).getConnection();
            SqlSettedStatement sqlStatement = new SqlSettedStatement(connection);
            sqlConn = sqlStatement.getSqlConn();
            sqlStatement.setSql(sql);
            name = BICubeDBUtils.getFirstColumnName(connection, sqlStatement, sql);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        } finally {
            DBUtils.closeConnection(sqlConn);
        }
        return name;
    }

    private ICubeFieldSource getCubeFieldSource(ICubeFieldSource[] fields, String columnName) {
        ICubeFieldSource iCubeFieldSource = null;
        for (ICubeFieldSource field : fields) {
            if (ComparatorUtils.equals(field.getFieldName(), columnName)) {
                iCubeFieldSource = field;
            }
        }
        return iCubeFieldSource;
    }

}
