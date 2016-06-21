package com.fr.bi.cal.stable.index;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.singletable.TableUpdate;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.SQLStatement;
import com.fr.bi.stable.data.db.SqlSettedStatement;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.util.BICubeDBUtils;
import com.fr.data.core.db.ColumnInformation;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.dml.Table;
import com.fr.data.impl.Connection;
import com.fr.file.DatasourceManager;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by 小灰灰 on 2015/10/15.
 */
public class SimpleIndexIncreaseGenerator extends SimpleIndexGenerator {
    private transient ICubeTableService oldTi;

    public SimpleIndexIncreaseGenerator(TableCubeFile cube, CubeTableSource dataSource, Set<CubeTableSource> derivedDataSources, int version, BIRecord log, ICubeDataLoader loader) {
        super(cube, dataSource, derivedDataSources, version, log, loader);
        oldTi = loader.getTableIndex(dataSource);
    }


    @Override
    protected long writeSimpleIndex() {
        int oldCount = loadOldValue();
        TreeSet<Integer> sortRemovedList = new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
        for (int i = 0; i < oldTi.getRemovedList().size(); i++) {
            sortRemovedList.add(oldTi.getRemovedList().get(i));
        }
        oldCount = writeData(sortRemovedList, oldCount);
        return oldCount;
    }


    private int writeData(TreeSet<Integer> sortRemovedList, int oldCount) {
        TableUpdate action = BICubeConfigureCenter.getTableUpdateManager().getSingleTableUpdateAction(dataSource.getPersistentTable());
        oldCount = dealWithInsert(action, oldCount);
        oldCount = dealWithModify(action, oldCount, sortRemovedList);
        dealWithRemove(action, sortRemovedList);
        return oldCount;
    }

    private int dealWithInsert(TableUpdate action, int rowCount) {
        String iSql = action.getInsert(loader);
        if (StringUtils.isEmpty(iSql)) {
            return rowCount;
        }
        DBTableSource source = (DBTableSource) dataSource;
        com.fr.data.impl.Connection connection = DatasourceManager.getInstance().getConnection(source.getDbName());
        SqlSettedStatement sqlStatement = new SqlSettedStatement(connection);
        sqlStatement.setSql(iSql);
        return BICubeDBUtils.runSQL(sqlStatement, cube.getBIField(), new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                cube.addDataValue(data);
            }
        }, rowCount);
    }

    private int dealWithModify(TableUpdate action, int rowCount, final TreeSet<Integer> sortRemovedList) {
        String mSql = action.getModify(loader);
        if (StringUtils.isEmpty(mSql)) {
            return rowCount;
        }
        DBTableSource source = (DBTableSource) dataSource;
        Connection connection = BIConnectionManager.getInstance().getConnection(source.getDbName());
        SqlSettedStatement sqlStatement = new SqlSettedStatement(connection);
        String columnName = getColumnName(connection, sqlStatement, mSql);
        try {
            Dialect dialect = DialectFactory.generateDialect(sqlStatement.getSqlConn(), connection.getDriver());
            Table table = new Table(BIConnectionManager.getInstance().getSchema(source.getDbName()), source.getTableName());
            String modifySql = "SELECT *" + " FROM " + dialect.table2SQL(table) + " t" + " WHERE " + "t." + columnName + " IN " + "(" + mSql + ")";
            sqlStatement.setSql(modifySql);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return rowCount;
        }
        int index = -1;
        ICubeFieldSource[] fields = cube.getBIField();
        for (int i = 0; i < fields.length; i++) {
            if (ComparatorUtils.equals(fields[i].getFieldName(), columnName)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            BILogger.getLogger().error("can not find field " + columnName);
            return rowCount;
        }
        BIKey key = new IndexKey(columnName);
        final ICubeColumnIndexReader getter = oldTi.loadGroup(key);
        final int fIndex = index;
        return BICubeDBUtils.runSQL(sqlStatement, cube.getBIField(), new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                cube.addDataValue(data);
                if (data.getCol() == fIndex) {
                    Object[] key = getter.createKey(1);
                    key[0] = data.getValue();
                    getter.getGroupIndex(key)[0].Traversal(new SingleRowTraversalAction() {
                        @Override
                        public void actionPerformed(int data) {
                            sortRemovedList.add(data);
                        }
                    });
                }
            }
        }, rowCount);
    }

    private void dealWithRemove(TableUpdate action, final TreeSet<Integer> sortRemovedList) {
        String rSql = action.getRemove(loader);
        if (StringUtils.isEmpty(rSql)) {
            return;
        }
        DBTableSource source = (DBTableSource) dataSource;
        Connection connection = BIConnectionManager.getInstance().getConnection(source.getDbName());
        SqlSettedStatement sqlStatement = new SqlSettedStatement(connection);
        sqlStatement.setSql(rSql);
        String columnName = getColumnName(connection, sqlStatement, rSql);
        ICubeFieldSource f = null;
        for (ICubeFieldSource field : cube.getBIField()) {
            if (ComparatorUtils.equals(field.getFieldName(), columnName)) {
                f = field;
                break;
            }
        }
        if (f == null) {
            BILogger.getLogger().error("can not find field " + columnName);
            return;
        }
        BIKey key = new IndexKey(columnName);
        final ICubeColumnIndexReader getter = oldTi.loadGroup(key);
        BICubeDBUtils.runSQL(sqlStatement, new ICubeFieldSource[]{f}, new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                Object[] key = getter.createKey(1);
                key[0] = data.getValue();
                getter.getGroupIndex(key)[0].Traversal(new SingleRowTraversalAction() {
                    @Override
                    public void actionPerformed(int data) {
                        sortRemovedList.add(data);
                    }
                });
            }
        });
        cube.writeRemovedLine(sortRemovedList);
    }

    private String getColumnName(com.fr.data.impl.Connection connection, SQLStatement statement, String sql) {
        try {
            java.sql.Connection conn = statement.getSqlConn();
            Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
            ColumnInformation column = DBUtils.checkInColumnInformation(conn, dialect, sql)[0];
            return column.getColumnName();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return StringUtils.EMPTY;
    }

    protected int loadOldValue() {
        BILogger.getLogger().info("now loading：" + dataSource.fetchObjectCore() + " old cube");
        new TableCubeFile(BIPathUtils.createTableTempPath(dataSource.fetchObjectCore().getID().getIdentityValue(), loader.getUserId())).copyDetailValue(cube, loader.getNIOReaderManager(), oldTi.getRowCount());
        BILogger.getLogger().info("loading：" + dataSource.fetchObjectCore() + " old cube finished");
        return oldTi.getRowCount();
    }
}