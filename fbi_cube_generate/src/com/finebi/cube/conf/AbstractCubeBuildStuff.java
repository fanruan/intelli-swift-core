package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.impl.conf.CalculateDependManager;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.utils.BITableRelationUtils;
import com.finebi.cube.utils.CubePreConditionsCheck;
import com.finebi.cube.utils.CubePreConditionsCheckManager;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.data.impl.Connection;
import com.fr.stable.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by kary on 16/7/11.
 */
public abstract class AbstractCubeBuildStuff implements CubeBuildStuff {
    private long userId;
    protected Set<CubeTableSource> allTableSources = new HashSet<CubeTableSource>();
    protected Set<BITableRelationPath> allRelationPathSet = new HashSet<BITableRelationPath>();
    protected Map<CubeTableSource, Map<String, ICubeFieldSource>> tableDBFieldMaps = new HashMap<CubeTableSource, Map<String, ICubeFieldSource>>();
    protected CalculateDependTool calculateDependTool;
    protected BISystemConfigHelper configHelper;

    public AbstractCubeBuildStuff(long userId) {
        this.userId = userId;
        configHelper = new BISystemConfigHelper(userId);
        allTableSources.addAll(configHelper.extractTableSource(configHelper.getSystemBusinessTables()));
        init(userId);
    }

    private void init(long userId) {
        calculateDependTool = new CalculateDependManager();
        try {
            allRelationPathSet = BICubeConfigureCenter.getTableRelationManager().getAllTablePath(userId);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public ICubeConfiguration getCubeConfiguration() {
        return BICubeConfiguration.getTempConf(Long.toString(userId));
    }

    protected Set<List<Set<CubeTableSource>>> calculateTableSource(Set<CubeTableSource> tableSources) {
        Iterator<CubeTableSource> it = tableSources.iterator();
        Set<List<Set<CubeTableSource>>> depends = new HashSet<List<Set<CubeTableSource>>>();
        while (it.hasNext()) {
            CubeTableSource tableSource = it.next();
            depends.add(tableSource.createGenerateTablesList());
        }
        return depends;
    }

    @Override
    public Map<CubeTableSource, Long> getVersions() {
        Set<CubeTableSource> allTable = getSingleSourceLayers();
        Map<CubeTableSource, Long> result = new HashMap<CubeTableSource, Long>();
        Long version = System.currentTimeMillis();
        for (CubeTableSource table : allTable) {
            result.put(table, version);
        }
        return result;
    }

    /**
     * edit by kary 2016-09-12
     * 新增数据连接有效性检查和SQL语句检查
     * 只对传统的关系型数据库有效
     * 对所有用到的连接进行检查
     * 暂时不对原SQL语句正确性进行检查，有视图的情况下太耗时了
     */
    @Override
    public boolean preConditionsCheck() {
        BILoggerFactory.getLogger().info("***************space check start*****************");
        boolean spaceCheck = getSpaceCheckResult();
        BILoggerFactory.getLogger().info("***************space check result: " + spaceCheck);
//        BILoggerFactory.getLogger().info("***************connection check start*****************");
//        boolean connectionCheck = getConnectionCheck();
//        BILoggerFactory.getLogger().info("***************connection check result: " + connectionCheck);
//        BILoggerFactory.getLogger().info("***************table check start*****************");
//        boolean sqlTest = getSqlTest();
//        BILoggerFactory.getLogger().info("***************table  check result: " + sqlTest);
//        return spaceCheck && connectionCheck;
        return spaceCheck;
    }

    public Set<CubeTableSource> getSystemTableSources() {
        return this.allTableSources;
    }

    public static Set<CubeTableSource> set2Set(Set<List<Set<CubeTableSource>>> set) {
        Set<CubeTableSource> result = new HashSet<CubeTableSource>();
        Iterator<List<Set<CubeTableSource>>> outIterator = set.iterator();
        while (outIterator.hasNext()) {
            Iterator<Set<CubeTableSource>> middleIterator = outIterator.next().iterator();
            while (middleIterator.hasNext()) {
                result.addAll(middleIterator.next());
            }
        }
        return result;
    }

    @Override
    public boolean isSingleTable() {
        return false;
    }

    /**
     * rename advanced to temp
     * rename tCube to advanced
     * delete temp
     *
     * @return
     */
    @Override
    public boolean replaceOldCubes() {
        ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(Long.toString(userId));
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(Long.toString(userId));
        ICubeConfiguration advancedTempConf = BICubeConfiguration.getAdvancedTempConf(Long.toString(userId));
        String advancedPath = advancedConf.getRootURI().getPath();
        String tCubePath = tempConf.getRootURI().getPath();
        String tempFolderPath = advancedTempConf.getRootURI().getPath();
        try {
            if (new File(advancedPath).exists()) {
                if (new File(tempFolderPath).exists()) {
                    boolean tempFolderDelete = BIFileUtils.delete(new File(tempFolderPath));
                    if (!tempFolderDelete) {
                        BILoggerFactory.getLogger().error("delete tempFolder failed");
                        return false;
                    }
                }
                boolean renameFolder = BIFileUtils.renameFolder(new File(advancedPath), new File(tempFolderPath));
                if (!renameFolder) {
                    BILoggerFactory.getLogger().error("rename Advanced to tempFolder failed");
                    return false;
                }
            }
            if (new File(tCubePath).exists()) {
                boolean renameFolder = BIFileUtils.renameFolder(new File(tCubePath), new File(advancedPath));
                if (!renameFolder) {
                    BILoggerFactory.getLogger().error("rename tCube to Advanced failed");
                    return false;
                }
            }
            //tCube替换（重命名）成功后删除tempFolder
            if (new File(tempFolderPath).exists()) {
                boolean deleteTempFolder = BIFileUtils.delete(new File(tempFolderPath));
                if (!deleteTempFolder) {
                    BILoggerFactory.getLogger().error("delete tempFolder failed ");
                }
            }
            return true;
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            return false;
        }
    }


    protected UpdateSettingSource setUpdateTypes(CubeTableSource source) {
        switch (source.getType()) {
            case BIBaseConstant.TABLETYPE.ETL:
                return getEtlUpdateType(source);
            default:
                return getSingleSourceUpdateType(source);
        }

    }

    private UpdateSettingSource getEtlUpdateType(CubeTableSource source) {
        UpdateSettingSource updateSettingSource = new UpdateSettingSource();
        Map<Integer, Set<CubeTableSource>> tableMaps = source.createGenerateTablesMap();
        boolean needUpdate = false;
        loop:
        for (Integer integer : tableMaps.keySet()) {
            for (CubeTableSource tableSource : tableMaps.get(integer)) {
                if (tableSource.getType() != BIBaseConstant.TABLETYPE.ETL) {
                    if (getSingleSourceUpdateType(tableSource).getUpdateType() == DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL) {
                        needUpdate = true;
                        break loop;
                    }
                }
            }
        }
        if (needUpdate) {
            updateSettingSource.setUpdateType(DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL);
        } else {
            updateSettingSource.setUpdateType(DBConstant.SINGLE_TABLE_UPDATE_TYPE.NEVER);
        }
        return updateSettingSource;
    }

    private UpdateSettingSource getSingleSourceUpdateType(CubeTableSource source) {
        UpdateSettingSource updateSettingSource = BIConfigureManagerCenter.getUpdateFrequencyManager().getTableUpdateSetting(source.getSourceID(), userId);
        if (null == updateSettingSource) {
            updateSettingSource = new UpdateSettingSource();
            updateSettingSource.setUpdateType(DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL);
        }
        return updateSettingSource;
    }


    protected boolean isTableRelationAvailable(BITableRelation relation, ICubeConfiguration cubeConfiguration) {
        if (configHelper.isTableRelationValid(relation)) {
            return BITableRelationUtils.isRelationAvailable(relation, cubeConfiguration);
        } else {
            return false;
        }
    }

    protected BITableSourceRelationPath convertPath(BITableRelationPath path) throws BITablePathConfusionException {
        return configHelper.convertPath(path);
    }

    protected Set<BITableSourceRelation> removeDuplicateRelations(Set<BITableSourceRelation> tableRelations) {
        Set<BITableSourceRelation> set = new HashSet<BITableSourceRelation>();
        Map sourceIdMap = new HashMap<String, BITableSourceRelation>();
        for (BITableSourceRelation relation : tableRelations) {
            try {
                if (!sourceIdMap.containsKey(relation.toString())) {
                    set.add(relation);
                    sourceIdMap.put(relation.toString(), relation);
                }
            } catch (NullPointerException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
        sourceIdMap.clear();
        return set;
    }

    protected Set<BITableSourceRelationPath> removeDuplicateRelationPaths(Set<BITableSourceRelationPath> paths) {
        Set<BITableSourceRelationPath> set = new HashSet<BITableSourceRelationPath>();
        Map sourceIdMap = new HashMap<String, BITableSourceRelationPath>();
        for (BITableSourceRelationPath path : paths) {
            try {
                if (!sourceIdMap.containsKey(path.getSourceID())) {
                    set.add(path);
                    sourceIdMap.put(path.getSourceID(), path);
                }
            } catch (NullPointerException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
        sourceIdMap.clear();
        return set;
    }

    private boolean getConnectionCheck() {
        CubePreConditionsCheck check = new CubePreConditionsCheckManager();
        Set<Connection> connectionSet = new HashSet<Connection>();
        double[] SqlSourceTypes = {BIBaseConstant.TABLETYPE.DB, BIBaseConstant.TABLETYPE.SQL};
        for (CubeTableSource tableSource : getSingleSourceLayers()) {
            if (ArrayUtils.contains(SqlSourceTypes, tableSource.getType())) {
                if (!connectionSet.contains(((DBTableSource) tableSource).getConnection())) {
                    if (!check.ConnectionCheck(((DBTableSource) tableSource).getConnection())) {
                        BILoggerFactory.getLogger().error("the table:" + tableSource.getTableName() + " connection test failed");
                        return false;
                    }
                    connectionSet.add(((DBTableSource) tableSource).getConnection());
                }
            }
        }
        return true;
    }

    private boolean getSpaceCheckResult() {
        CubePreConditionsCheck check = new CubePreConditionsCheckManager();
        ICubeConfiguration conf = BICubeConfiguration.getConf(String.valueOf(userId));
        return check.HDSpaceCheck(new File(conf.getRootURI().getPath()));
    }

    private boolean getSqlTest() {
        BICube cube = new BICube(new BICubeResourceRetrieval(getCubeConfiguration()), BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        for (CubeTableSource cubeTableSource : getSingleSourceLayers()) {
            CubePreConditionsCheck check = new CubePreConditionsCheckManager();
            boolean SqlCheckResult = check.SQLCheck(cube, cubeTableSource);
            if (!SqlCheckResult) {
                BILoggerFactory.getLogger().error("the table:" + cubeTableSource.getTableName() + " sql test failed");
                return false;
            }
        }
        return true;
    }
}
