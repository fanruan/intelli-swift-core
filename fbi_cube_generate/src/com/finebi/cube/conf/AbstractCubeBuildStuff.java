package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.impl.conf.CalculateDependManager;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.utils.BIDataStructTranUtils;
import com.finebi.cube.utils.CubePreConditionsCheck;
import com.finebi.cube.utils.CubePreConditionsCheckManager;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.data.impl.Connection;
import com.fr.general.ComparatorUtils;
import com.fr.stable.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kary on 16/7/11.
 */
public abstract class AbstractCubeBuildStuff implements CubeBuildStuff {
    private static BILogger LOGGER = BILoggerFactory.getLogger(AbstractCubeBuildStuff.class);
    protected long userId;
    protected Set<CubeTableSource> allTableSources = new HashSet<CubeTableSource>();
    protected CalculateDependTool calculateDependTool;
    protected Set<String> taskTableSourceIDs;
    private final ICubeConfiguration cubeConfiguration;

    public AbstractCubeBuildStuff(long userId, Set<CubeTableSource> allTableSources) {
        this.userId = userId;
        this.allTableSources.addAll(allTableSources);
        calculateDependTool = new CalculateDependManager();
        cubeConfiguration = BICubeConfiguration.getTempConf(Long.toString(userId));
    }

    @Override
    public ICubeConfiguration getCubeConfiguration() {
        return cubeConfiguration;
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
        LOGGER.info("***************space check start*****************");
        boolean spaceCheck = hasSpace();
        LOGGER.info("***************space check result: " + spaceCheck + " *****************");
        return spaceCheck;
    }

    public Set<CubeTableSource> getSystemTableSources() {
        return this.allTableSources;
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
        ICubeConfiguration tCubeConf = cubeConfiguration;
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(Long.toString(userId));
        ICubeConfiguration advancedTempConf = BICubeConfiguration.getAdvancedTempConf(Long.toString(userId));
        String advancedPath = advancedConf.getRootURI().getPath();
        String tCubePath = tCubeConf.getRootURI().getPath();
        String tempFolderPath = advancedTempConf.getRootURI().getPath();
        try {
            if (new File(advancedPath).exists()) {
                LOGGER.info("Advanced cube exist, start to rename advanced cube");
                boolean renameFolder = BIFileUtils.renameFolder(new File(advancedPath), new File(tempFolderPath + System.currentTimeMillis()));
                if (!renameFolder) {
                    LOGGER.error("rename Advanced to tempFolder failed");
                    return false;
                }
            }
            if (new File(tCubePath).exists()) {
                LOGGER.info("Start to rename tCube to advanced");
                boolean renameFolder = BIFileUtils.renameFolder(new File(tCubePath), new File(advancedPath));
                if (!renameFolder) {
                    LOGGER.error("rename FineIndex to Advanced failed");
                    return false;
                }
            }

            //删除除了advanced 跟tCube之外的temp文件夹
            LOGGER.info("Start to delete temp folder");
            deleteTempFolders();
            LOGGER.info("*******Finish replace cube*********");
            return true;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    protected void deleteTempFolders() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        String tCubePath = getCubeConfiguration().getRootURI().getPath();
        String advancedPath = BICubeConfiguration.getConf(Long.toString(userId)).getRootURI().getPath();

        File advancedFile = new File(advancedPath);
        if (advancedFile.getParentFile().exists()) {
            File[] files = advancedFile.getParentFile().listFiles();

            for (final File file : files) {
                if (!file.getAbsolutePath().equals(advancedFile.getAbsolutePath()) && !file.getAbsolutePath().equals(new File(tCubePath).getAbsolutePath())) {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            BIFileUtils.delete(file);
                        }
                    });
                }
            }
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
        UpdateSettingSource updateSettingSource = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSetting(source.getSourceID(), userId);
        if (null == updateSettingSource) {
            updateSettingSource = new UpdateSettingSource();
            updateSettingSource.setUpdateType(DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL);
        }
        return updateSettingSource;
    }

    protected Set<BITableSourceRelation> filterRelations(Set<BITableSourceRelation> tableRelations) {
        Set<BITableSourceRelation> relations = removeDuplicateRelations(tableRelations);
        return removeSelfRelations(relations);
    }

    private Set<BITableSourceRelation> removeSelfRelations(Set<BITableSourceRelation> tableRelations) {
        Set<BITableSourceRelation> set = new HashSet<BITableSourceRelation>();
        for (BITableSourceRelation relation : tableRelations) {
            if (!ComparatorUtils.equals(relation.getPrimaryTable().getSourceID(), relation.getForeignTable().getSourceID())) {
                set.add(relation);
            }
        }
        return set;
    }

    private Set<BITableSourceRelation> removeDuplicateRelations(Set<BITableSourceRelation> tableRelations) {
        Set<BITableSourceRelation> set = new HashSet<BITableSourceRelation>();
        Map sourceIdMap = new HashMap<String, BITableSourceRelation>();
        for (BITableSourceRelation relation : tableRelations) {
            try {
                if (!sourceIdMap.containsKey(relation.toString())) {
                    set.add(relation);
                    sourceIdMap.put(relation.toString(), relation);
                }
            } catch (NullPointerException e) {
                LOGGER.error(e.getMessage(), e);
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
                LOGGER.error(e.getMessage(), e);
                continue;
            }
        }
        sourceIdMap.clear();
        return set;
    }

    private boolean hasConnection() {
        CubePreConditionsCheck check = new CubePreConditionsCheckManager();
        Set<Connection> connectionSet = new HashSet<Connection>();
        double[] SqlSourceTypes = {BIBaseConstant.TABLETYPE.DB, BIBaseConstant.TABLETYPE.SQL};
        for (CubeTableSource tableSource : getSingleSourceLayers()) {
            if (ArrayUtils.contains(SqlSourceTypes, tableSource.getType())) {
                if (!connectionSet.contains(((DBTableSource) tableSource).getConnection())) {
                    if (!check.ConnectionCheck(((DBTableSource) tableSource).getConnection())) {
                        LOGGER.error("the table:" + tableSource.getTableName() + " connection test failed");
                        return false;
                    }
                    connectionSet.add(((DBTableSource) tableSource).getConnection());
                }


            }
        }
        return true;
    }

    private boolean envCheck() {
        CubePreConditionsCheck check = new CubePreConditionsCheckManager();
        check.envCheck(userId);
        return true;

    }

    private boolean hasSpace() {
        CubePreConditionsCheck check = new CubePreConditionsCheckManager();
        ICubeConfiguration conf = BICubeConfiguration.getConf(String.valueOf(userId));
        return check.HDSpaceCheck(new File(conf.getRootURI().getPath()));
    }

    protected Set<String> getDependTableSourceIdSet(Set<List<Set<CubeTableSource>>> dependTableSource) {
        Set<String> tableSourceIDSet = new HashSet<String>();
        for (CubeTableSource tableSource : BIDataStructTranUtils.set2Set(dependTableSource)) {
            if (tableSource != null) {
                tableSourceIDSet.add(tableSource.getSourceID());
            }
        }
        return tableSourceIDSet;
    }


}
