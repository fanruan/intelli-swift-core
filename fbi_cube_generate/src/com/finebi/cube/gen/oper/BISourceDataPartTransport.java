package com.finebi.cube.gen.oper;

import com.finebi.cube.adapter.BIUserCubeManager;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.Cube;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.SqlSettedStatement;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.utils.SQLRegUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.util.BICubeDBUtils;
import com.fr.data.impl.Connection;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.bridge.StableFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.fr.bi.util.BICubeDBUtils.getColumnName;

/**
 * Created by kary on 16/7/13.
 */
public class BISourceDataPartTransport extends BISourceDataTransport {
    public BISourceDataPartTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version) {
        super(cube, tableSource, allSources, parentTableSource, version);
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        long t = System.currentTimeMillis();
        try {
            super.recordTableInfo();
            long count = transport();
            if (count >= 0) {
                tableEntityService.recordRowCount(count);
            }
            tableEntityService.addVersion(version);
            long tableCostTime = System.currentTimeMillis() - t;
            if (null != tableSource.getPersistentTable()) {
                System.out.println("table usage:" + tableCostTime);
                biLogManager.infoTable(tableSource.getPersistentTable(), tableCostTime, UserControl.getInstance().getSuperManagerID());
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            if (null != tableSource.getPersistentTable()) {
                biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
            }
        } finally {
            return null;
        }
    }

    private long transport() {
        List<ICubeFieldSource> fieldList = tableEntityService.getFieldInfo();
        ICubeFieldSource[] cubeFieldSources = new ICubeFieldSource[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++) {
            fieldList.get(i).setTableBelongTo(tableSource);
            cubeFieldSources[i] = fieldList.get(i);
        }
        DBTableSource source = (DBTableSource) this.tableSource;
        UpdateSettingSource tableUpdateSetting = BIConfigureManagerCenter.getUpdateFrequencyManager().getTableUpdateSetting(tableSource.getSourceID(), UserControl.getInstance().getSuperManagerID());
        source.setUpdateSettingSource(tableUpdateSetting);
        long rowCount = tableEntityService.isVersionAvailable() ? tableEntityService.getRowCount() : 0;
        TreeSet<Integer> sortRemovedList = new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
        BIUserCubeManager loader = new BIUserCubeManager(UserControl.getInstance().getSuperManagerID(), cube);
        /*add*/
        if (StringUtils.isNotEmpty(tableUpdateSetting.getPartAddSQL())) {
            rowCount = dealWidthAdd(cubeFieldSources, addDateCondition(tableUpdateSetting.getPartAddSQL()), rowCount);
        }
        /*remove*/
        if (StringUtils.isNotEmpty(tableUpdateSetting.getPartDeleteSQL())) {
            sortRemovedList = dealWithRemove(cubeFieldSources, addDateCondition(tableUpdateSetting.getPartDeleteSQL()), sortRemovedList, loader);
        }
        /*modify*/
        if (StringUtils.isNotEmpty(tableUpdateSetting.getPartModifySQL())) {
            rowCount = dealWidthAdd(cubeFieldSources, addDateCondition(tableUpdateSetting.getPartModifySQL()), rowCount);
            sortRemovedList = dealWithRemove(cubeFieldSources, tableUpdateSetting.getPartModifySQL(), sortRemovedList, loader);
        }
        if (null != sortRemovedList) {
            tableEntityService.recordRemovedLine(sortRemovedList);
        }

        return rowCount;
    }


    private long dealWidthAdd(ICubeFieldSource[] cubeFieldSources, String SQL, long rowCount) {
        Traversal<BIDataValue> AddTraversal = new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue v) {
                try {
                    tableEntityService.addDataValue(v);
                } catch (BICubeColumnAbsentException e) {
                    e.printStackTrace();
                }
            }
        };

        rowCount = tableSource.read4Part(AddTraversal, cubeFieldSources, SQL, rowCount);
        return rowCount;
    }


    private TreeSet<Integer> dealWithRemove(ICubeFieldSource[] fields, String partDeleteSQL, final TreeSet<Integer> sortRemovedList, ICubeDataLoader loader) {
        SQLRegUtils regUtils = new SQLRegUtils(partDeleteSQL);
        if (!regUtils.isSql()) {
            BILogger.getLogger().error("SQL syntax error");
            return null;
        }
        Connection connection = BIConnectionManager.getInstance().getConnection(((DBTableSource) tableSource).getDbName());
        SqlSettedStatement sqlStatement = new SqlSettedStatement(connection);
        sqlStatement.setSql(partDeleteSQL);
        String columnName = getColumnName(connection, sqlStatement, partDeleteSQL);
        ICubeFieldSource f = null;
        for (ICubeFieldSource field : fields) {
            if (ComparatorUtils.equals(field.getFieldName(), columnName)) {
                f = field;
                break;
            }
        }
        if (f == null) {
            BILogger.getLogger().error("can not find field " + columnName);
            return null;
        }
        BIKey key = new IndexKey(columnName);
        ICubeTableService oldTi = loader.getTableIndex(tableSource);
        final ICubeColumnIndexReader getter = oldTi.loadGroup(key);
        Traversal<BIDataValue> removeTraversal = new Traversal<BIDataValue>() {
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
        };
        BICubeDBUtils.runSQL(sqlStatement, new ICubeFieldSource[]{f}, removeTraversal);
        return sortRemovedList;
    }

    private String addDateCondition(String sql) {
        String LastModifyTime = "${上次更新时间}";
        if (!sql.contains(LastModifyTime)) {
            return sql;
        }
        SQLRegUtils sqlRegUtils = new SQLRegUtils(sql);
        String conditions = sqlRegUtils.getConditions();
        if (tableEntityService.isCubeLastTimeAvailable() && null != tableEntityService.getCubeLastTime()) {
            Date lastTime = tableEntityService.getCubeLastTime();
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
            String dateStr = sdf.format(lastTime);
            conditions = conditions.replace(LastModifyTime, dateStr);
            sqlRegUtils.setConditions(conditions);
        } else
            conditions = conditions.replace(LastModifyTime, "");
        sqlRegUtils.setConditions(conditions);
        return sqlRegUtils.toString();
    }

}
