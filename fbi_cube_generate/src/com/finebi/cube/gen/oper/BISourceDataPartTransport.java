package com.finebi.cube.gen.oper;

import com.finebi.cube.adapter.BIUserCubeManager;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BICubeConfiguration;
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
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.data.DBQueryExecutor;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.SqlSettedStatement;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.SQLRegUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.bridge.StableFactory;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fr.bi.util.BICubeDBUtils.getColumnName;

/**
 * Created by kary on 16/7/13.
 */
public class BISourceDataPartTransport extends BISourceDataTransport {
    protected UpdateSettingSource tableUpdateSetting;

    public BISourceDataPartTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version, UpdateSettingSource tableUpdateSetting) {
        super(cube, tableSource, allSources, parentTableSource, version);
        this.tableUpdateSetting = tableUpdateSetting;
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        long t = System.currentTimeMillis();
        try {
            copyFromOldCubes();
            super.recordTableInfo();
            long count = transport();
            ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
            ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getTempConf(String.valueOf(UserControl.getInstance().getSuperManagerID())));
            cube = new BICube(resourceRetrievalService, discovery);
            tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
            if (count >= 0) {
                tableEntityService.recordRowCount(count);
            }
            tableEntityService.addVersion(version);
            tableEntityService.clear();
            long tableCostTime = System.currentTimeMillis() - t;
            System.out.println("table usage:" + tableCostTime);
            try {
                biLogManager.infoTable(tableSource.getPersistentTable(), tableCostTime, UserControl.getInstance().getSuperManagerID());
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        } catch (Exception e) {
            try {
                biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
            } catch (Exception e1) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
            BILogger.getLogger().error(e.getMessage(), e);
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

        long rowCount = tableEntityService.isVersionAvailable() ? tableEntityService.getRowCount() : 0;
        TreeSet<Integer> sortRemovedList = new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
        if (tableEntityService.isRemovedListAvailable()) {
            IntList removedList = tableEntityService.getRemovedList();
            for (int i = 0; i < removedList.size(); i++) {
                sortRemovedList.add(Integer.valueOf(removedList.get(i)));
            }
        }
        BIUserCubeManager loader = new BIUserCubeManager(UserControl.getInstance().getSuperManagerID(), cube);

          /*remove*/
        if (StringUtils.isNotEmpty(tableUpdateSetting.getPartDeleteSQL())) {
            sortRemovedList = dealWithRemove(cubeFieldSources, addDateCondition(tableUpdateSetting.getPartDeleteSQL()), sortRemovedList, loader);
        }
        /*add*/
        if (StringUtils.isNotEmpty(tableUpdateSetting.getPartAddSQL())) {
            rowCount = dealWidthAdd(cubeFieldSources, addDateCondition(tableUpdateSetting.getPartAddSQL()), rowCount);
        }
        /*modify*/
        if (StringUtils.isNotEmpty(tableUpdateSetting.getPartModifySQL())) {
            sortRemovedList = dealWithRemove(cubeFieldSources, tableUpdateSetting.getPartModifySQL(), sortRemovedList, loader);
            rowCount = dealWidthAdd(cubeFieldSources, addDateCondition(tableUpdateSetting.getPartModifySQL()), rowCount);
        }
        if (null != sortRemovedList && sortRemovedList.size() != 0) {
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
                    BILogger.getLogger().error(e.getMessage());
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
        com.fr.data.impl.Connection connection = ((DBTableSource) tableSource).getConnection();
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
        DBQueryExecutor.getInstance().runSQL(sqlStatement, new ICubeFieldSource[]{f}, removeTraversal);
        return sortRemovedList;
    }

    private String addDateCondition(String sql) {
        if (tableEntityService.isCubeLastTimeAvailable() && null != tableEntityService.getCubeLastTime()) {
            Date lastTime = tableEntityService.getCubeLastTime();
            Pattern pat = Pattern.compile("\\$[\\{][^\\}]*[\\}]");
            Matcher matcher = pat.matcher(sql);
            String dateStr = DateUtils.DATETIMEFORMAT2.format(lastTime);
            while (matcher.find()) {
                String matchStr = matcher.group(0);
                sql = sql.replace(matchStr, dateStr);
            }
        }
        return sql;
    }


}
