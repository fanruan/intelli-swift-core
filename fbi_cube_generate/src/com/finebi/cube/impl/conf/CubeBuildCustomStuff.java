package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.AbstractCubeBuildStuff;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.gen.oper.BuildLogHelper;
import com.finebi.cube.impl.conf.helpers.DependsCalculateHelper;
import com.finebi.cube.impl.conf.helpers.DuplicateFilterHelper;
import com.finebi.cube.impl.conf.helpers.PathCalculateHelper;
import com.finebi.cube.impl.conf.helpers.RelationCalculateHelper;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.utils.BIDataStructTranUtils;
import com.fr.bi.conf.data.source.TableSourceUtils;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.stable.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Lucifer on 2017-5-23.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CubeBuildCustomStuff extends AbstractCubeBuildStuff {

    private static BILogger LOGGER = BILoggerFactory.getLogger(CubeBuildCustomStuff.class);

    Map<CubeTableSource, Set<String>> tableBaseSourceIdMap;
    Map<String, Integer> sourceIdUpdateTypeMap;

    private Set<CubeTableSource> tableLayers;
    private Set<CubeTableSource> tableInConstruction;
    private Set<BITableSourceRelation> relationInConstruction;
    private Set<BITableSourceRelationPath> pathInConstruction;
    private Set<BICubeGenerateRelation> relationDepends;
    private Set<List<Set<CubeTableSource>>> tableSourceLayerDepends;
    private Set<BICubeGenerateRelationPath> pathDepends;
    private Set<String> allSourceIds = new HashSet<String>();
    private Set<String> basicSourceIds = new HashSet<String>();

    /**
     * @param userId
     * @param tableBaseSourceIdMap  所有tableSource和baseSourceId对应关系
     * @param sourceIdUpdateTypeMap 所有sourceId和updateType对应关系
     * @param tableSources          需要更新的tablesource
     * @param relations             需要更新的relation
     * @param paths                 需要更新的path
     * @param absentTables          缺失tablesource
     * @param absentRelations       缺失relation
     * @param absentPaths           缺失path
     */
    public CubeBuildCustomStuff(long userId, Map<CubeTableSource, Set<String>> tableBaseSourceIdMap,
                                Map<String, Integer> sourceIdUpdateTypeMap,
                                Set<CubeTableSource> tableSources, Set<BITableSourceRelation> relations,
                                Set<BITableSourceRelationPath> paths, Set<CubeTableSource> absentTables,
                                Set<BITableSourceRelation> absentRelations, Set<BITableSourceRelationPath> absentPaths,
                                Set<CubeTableSource> allTableSources,
                                Set<BITableSourceRelation> allRelations, Set<BITableSourceRelationPath> allPaths) {

        super(userId, allTableSources);
        this.tableBaseSourceIdMap = tableBaseSourceIdMap;
        this.sourceIdUpdateTypeMap = sourceIdUpdateTypeMap;

        boolean calcBasicSourceIds = true;
        for (Map.Entry<CubeTableSource, Set<String>> entry : this.tableBaseSourceIdMap.entrySet()) {
            basicSourceIds.addAll(entry.getValue());
            calcBasicSourceIds = false;
        }

        for (CubeTableSource tableSource : tableSources) {
            allSourceIds.add(tableSource.getSourceID());
            if (calcBasicSourceIds) {
                if (TableSourceUtils.isBasicTable(tableSource)) {
                    basicSourceIds.add(tableSource.getSourceID());
                }
            }
        }

        this.tableInConstruction = new HashSet<CubeTableSource>(tableSources);
        this.relationInConstruction = new HashSet<BITableSourceRelation>(relations);
        this.pathInConstruction = new HashSet<BITableSourceRelationPath>(paths);

        relationInConstruction.addAll(RelationCalculateHelper.calculateRelevantRelation(allSourceIds, allRelations));
        this.relationInConstruction = RelationCalculateHelper.removeRelationAbsentTable(this.tableInConstruction, this.relationInConstruction, absentTables, absentRelations);

        pathInConstruction.addAll(PathCalculateHelper.calculateRelevantPath(relationInConstruction, allPaths));
        this.pathInConstruction = PathCalculateHelper.removePathAbsentRelation(this.relationInConstruction, this.pathInConstruction, absentRelations, absentPaths);

        DuplicateFilterHelper.filterAll(tableInConstruction, relationInConstruction, pathInConstruction);

        this.tableSourceLayerDepends = DependsCalculateHelper.calculateSourceLayerDepends(tableInConstruction);
        this.tableLayers = DependsCalculateHelper.calculateSourceLayers(tableSourceLayerDepends);
        this.relationDepends = DependsCalculateHelper.calculateRelationDepends(calculateDependTool, relationInConstruction, tableInConstruction);
        this.pathDepends = DependsCalculateHelper.calculatePathDepends(calculateDependTool, pathInConstruction, relationInConstruction);

        DependsCalculateHelper.removeBasicTableInDepends(tableSourceLayerDepends, basicSourceIds);

    }

    @Override
    public Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet() {
        return this.pathDepends;
    }

    @Override
    public Set<CubeTableSource> getSingleSourceLayers() {
        return this.tableLayers;
    }

    @Override
    public Set<String> getTaskTableSourceIds() {
        if (taskTableSourceIDs == null) {
            taskTableSourceIDs = getDependTableSourceIdSet(tableSourceLayerDepends);
        }
        return taskTableSourceIDs;
    }

    @Override
    public String getCubeTaskId() {
        Object[] objects = allSourceIds.toArray();
        String[] ids = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            ids[i] = (String) objects[i];
        }
        return BIStringUtils.append(ids);
    }

    @Override
    public Set<BICubeGenerateRelation> getCubeGenerateRelationSet() {
        return this.relationDepends;
    }

    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.CUSTOM;
    }

    @Override
    public Set<BITableSourceRelationPath> getTableSourceRelationPathSet() {
        return this.pathInConstruction;
    }

    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return this.relationInConstruction;
    }

    @Override
    public Set<List<Set<CubeTableSource>>> getDependTableResource() {
        return this.tableSourceLayerDepends;
    }

    @Override
    public boolean replaceOldCubes() {
        ICubeConfiguration tempConf = getCubeConfiguration();
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(String.valueOf(userId));
        try {
            BIFileUtils.moveFile(tempConf.getRootURI().getPath().toString(), advancedConf.getRootURI().getPath().toString());
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public boolean copyFileFromOldCubes() {
        try {
            ICubeConfiguration tempConf = getCubeConfiguration();
            ICubeConfiguration advancedConf = BICubeConfiguration.getConf(String.valueOf(userId));
            BICubeResourceRetrieval tempResourceRetrieval = new BICubeResourceRetrieval(tempConf);
            BICubeResourceRetrieval advancedResourceRetrieval = new BICubeResourceRetrieval(advancedConf);
            if (new File(tempConf.getRootURI().getPath()).exists()) {
                BIFileUtils.delete(new File(tempConf.getRootURI().getPath()));
            }
            new File(tempConf.getRootURI().getPath()).mkdirs();
            Set<CubeTableSource> tableSet = choseTables();
            for (CubeTableSource source : tableSet) {
                copyTableFile(tempResourceRetrieval, advancedResourceRetrieval, source);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public Map<CubeTableSource, UpdateSettingSource> getUpdateSettingSources() {
        Map<CubeTableSource, UpdateSettingSource> map = new HashMap<CubeTableSource, UpdateSettingSource>();
        for (CubeTableSource source : this.getSingleSourceLayers()) {
            UpdateSettingSource updateSettingSource = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSetting(source.getSourceID(), userId);
            if (updateSettingSource == null) {
                updateSettingSource = new UpdateSettingSource();
            }
            if (source.getType() == BIBaseConstant.TABLETYPE.ETL) {  //如果是etl表，则看该etl是never更新还是all更新。
                updateSettingSource = getEtlUpdateType(source);
            } else { //如果是基础表，则看有没有传updatetype进来，传则用，没传则用默认更新方式。
                if (sourceIdUpdateTypeMap.containsKey(source.getSourceID())) {
                    updateSettingSource.setUpdateType(sourceIdUpdateTypeMap.get(source.getSourceID()));
                }
            }
            map.put(source, updateSettingSource);
        }
        return map;
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

    private Set<CubeTableSource> choseTables() {
        Set<CubeTableSource> tables = new HashSet<CubeTableSource>();
        tables.addAll(calculateTablesNeedNotGenerate());
        tables = BIDataStructTranUtils.set2Set(calculateTableSource(tables));
        return filterDuplicateTable(tables);
    }

    private Set<CubeTableSource> calculateTablesNeedNotGenerate() {
        Set<CubeTableSource> tables = new HashSet<CubeTableSource>();
        for (BITableSourceRelation relation : relationInConstruction) {
            tables.add(relation.getPrimaryTable());
        }
        for (BITableSourceRelationPath path : pathInConstruction) {
            try {
                tables.add(path.getFirstRelation().getPrimaryTable());
            } catch (BITablePathEmptyException e) {
                LOGGER.error(e.getMessage(), e);
                continue;
            }
        }
        tables.removeAll(tableInConstruction);
        return tables;
    }

    protected Set<CubeTableSource> filterDuplicateTable(Set<CubeTableSource> tableInConstruction) {
        LOGGER.info("filter duplicate table");
        Set<CubeTableSource> result = new HashSet<CubeTableSource>();
        Set<String> relationID = new HashSet<String>();
        for (CubeTableSource tableSource : tableInConstruction) {
            LOGGER.debug(BuildLogHelper.tableLogContent(StringUtils.EMPTY, tableSource));
            String id = tableSource.getSourceID();
            if (!relationID.contains(id)) {
                result.add(tableSource);
                relationID.add(id);
            } else {
                LOGGER.info("The table source id has present:\n" + BuildLogHelper.tableLogContent(StringUtils.EMPTY, tableSource));
            }
        }
        return result;
    }

    private void copyTableFile(ICubeResourceRetrievalService tempResourceRetrieval, ICubeResourceRetrievalService advancedResourceRetrieval, CubeTableSource source) throws BICubeResourceAbsentException, BITablePathEmptyException, IOException {
        ICubeResourceLocation from = advancedResourceRetrieval.retrieveResource(new BITableKey(source));
        ICubeResourceLocation to = tempResourceRetrieval.retrieveResource(new BITableKey(source));
        if (new File(from.getAbsolutePath()).exists()) {
            BIFileUtils.copyFolder(new File(from.getAbsolutePath()), new File(to.getAbsolutePath()));
        }
    }

    @Override
    public boolean isNeed2Update() {
        if (this.getDependTableResource().isEmpty() && this.getCubeGenerateRelationSet().isEmpty()) {
            if (this.getCubeGenerateRelationPathSet().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
