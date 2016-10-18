package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.impl.conf.CalculateDependManager;
import com.finebi.cube.impl.conf.CubePreConditionsCheckManager;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.utils.BITableRelationUtils;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.SQLTableSource;
import com.fr.bi.conf.data.source.ServerTableSource;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.data.impl.Connection;
import com.fr.file.DatasourceManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by kary on 16/7/11.
 */
public abstract class AbstractCubeBuild implements CubeBuild {
    private long userId;
    protected Set<CubeTableSource> sources = new HashSet<CubeTableSource>();
    private Set allBusinessTable = new HashSet<BIBusinessTable>();
    protected Set<BITableRelationPath> allRelationPathSet = new HashSet<BITableRelationPath>();
    protected Map<CubeTableSource, Map<String, ICubeFieldSource>> tableDBFieldMaps = new HashMap<CubeTableSource, Map<String, ICubeFieldSource>>();
    protected CalculateDependTool calculateDependTool;

    public AbstractCubeBuild(long userId) {
        this.userId = userId;
        init(userId);
        setSources();
        fullTableDBFields();
    }

    private void init(long userId) {
        allBusinessTable = BICubeConfigureCenter.getPackageManager().getAllTables(userId);
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
        Set<CubeTableSource> allTable = getAllSingleSources();
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
     * 连接检查现在直接调用预览模块，该模块现在在使用Hive时存在bug，等清掉那个bug再放开该检查
     */
    @Override
    public boolean preConditionsCheck() {
        CubePreConditionsCheck check = new CubePreConditionsCheckManager();
        ICubeConfiguration conf = BICubeConfiguration.getConf(String.valueOf(userId));
        boolean spaceCheck = check.HDSpaceCheck(new File(conf.getRootURI().getPath()));
        boolean connectionValid = true;
        /*暂时不对所有表做检测，有一张表连接失败即为失败*/
//        for (CubeTableSource source : getAllSingleSources()) {
//            boolean connectionCheck = check.ConnectionCheck(source, userId);
//            if (!connectionCheck) {
//                connectionValid = false;
//                String errorMessage ="error:"+source.getTableName() + ": Connection test failed";
//                BILoggerFactory.getLogger().error(errorMessage);
//                BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), errorMessage, userId);
//                break;
//            }
//        }
        return spaceCheck && connectionValid;
    }

    @Override
    public Set<CubeTableSource> getSources() {
        return this.sources;
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


    @Override
    public boolean replaceOldCubes() {
        ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(Long.toString(userId));
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(Long.toString(userId));
        ICubeConfiguration advancedTempConf = BICubeConfiguration.getAdvancedTempConf(Long.toString(userId));
        String advancedPath = advancedConf.getRootURI().getPath();
        String tCubePath = tempConf.getRootURI().getPath();
        String tempFolderPath = advancedTempConf.getRootURI().getPath();
        try {
            if (new File(tempFolderPath).exists()) {
                BIFileUtils.delete(new File(tempFolderPath));
            }
            if (new File(advancedPath).exists() && !new File(tempFolderPath).exists()) {
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

    public void setSources() {
        for (Object biBusinessTable : allBusinessTable) {
            BusinessTable table = (BusinessTable) biBusinessTable;
            sources.add(table.getTableSource());
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

    @Override
    public Map<CubeTableSource, Connection> getConnections() {
        Map<CubeTableSource, Connection> connectionMap = new HashMap<CubeTableSource, Connection>();
        for (CubeTableSource tableSource : getAllSingleSources()) {
            com.fr.data.impl.Connection connection = null;
            DatasourceManager.getInstance().getNameConnectionMap();
            if (tableSource instanceof DBTableSource) {
                connection = DatasourceManager.getInstance().getConnection(((DBTableSource) tableSource).getDbName());
                ((DBTableSource) tableSource).setConnection(connection);
            }
            if (tableSource instanceof SQLTableSource) {
                connection = DatasourceManager.getInstance().getConnection(((SQLTableSource) tableSource).getSqlConnection());
                ((ServerTableSource) tableSource).setConnection(connection);
            }
            connectionMap.put(tableSource, connection);
        }
        return connectionMap;
    }

    private void fullTableDBFields() {
        Iterator<CubeTableSource> tableSourceIterator = sources.iterator();
        while (tableSourceIterator.hasNext()) {
            CubeTableSource tableSource = tableSourceIterator.next();
            Set<ICubeFieldSource> BICubeFieldSources = tableSource.getFacetFields(sources);
            Map<String, ICubeFieldSource> name2Field = new HashMap<String, ICubeFieldSource>();
            Iterator<ICubeFieldSource> it = BICubeFieldSources.iterator();
            while (it.hasNext()) {
                ICubeFieldSource field = it.next();
                name2Field.put(field.getFieldName(), field);
            }
            tableDBFieldMaps.put(tableSource, name2Field);
        }
    }

    protected BITableSourceRelation convertRelation(BITableRelation relation) {
        CubeTableSource primaryTable;
        CubeTableSource foreignTable;
        try {
            primaryTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(relation.getPrimaryField().getTableBelongTo());
            foreignTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(relation.getForeignField().getTableBelongTo());
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
        ICubeFieldSource primaryField = tableDBFieldMaps.get(primaryTable).get(relation.getPrimaryField().getFieldName());
        ICubeFieldSource foreignField = tableDBFieldMaps.get(foreignTable).get(relation.getForeignField().getFieldName());
        if (!isTableRelationValid(relation)) {
            BILoggerFactory.getLogger().error("tableSourceRelation invalid:" + relation.toString());
            return null;
        }
        BITableSourceRelation biTableSourceRelation = new BITableSourceRelation(
                primaryField,
                foreignField,
                primaryTable,
                foreignTable
        );
        primaryField.setTableBelongTo(primaryTable);
        foreignField.setTableBelongTo(foreignTable);
        return biTableSourceRelation;
    }


    protected boolean isTableRelationValid(BITableRelation relation) {
        boolean relationValid = BITableRelationUtils.isRelationValid(relation);
        boolean isStructureValid = allBusinessTable.contains(relation.getPrimaryTable()) && allBusinessTable.contains(relation.getForeignTable());
        return isStructureValid && relationValid;
    }

    protected boolean isTableRelationAvailable(BITableRelation relation, ICubeConfiguration cubeConfiguration) {
        if (isTableRelationValid(relation)) {
            return BITableRelationUtils.isRelationAvailable(relation, cubeConfiguration);
        } else {
            return false;
        }
    }

    protected BITableSourceRelationPath convertPath(BITableRelationPath path) throws BITablePathConfusionException {
        BITableSourceRelationPath tableSourceRelationPath = new BITableSourceRelationPath();
        for (BITableRelation biTableRelation : path.getAllRelations()) {
            BITableSourceRelation biTableSourceRelation = convertRelation(biTableRelation);
            if (null == biTableSourceRelation) {
                return null;
            }
            tableSourceRelationPath.addRelationAtTail(biTableSourceRelation);
        }
        return tableSourceRelationPath;
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
}
