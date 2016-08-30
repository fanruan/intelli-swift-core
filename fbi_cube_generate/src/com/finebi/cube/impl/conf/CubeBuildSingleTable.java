package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.*;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.*;
import com.finebi.cube.structure.BITableKey;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.BIRelationUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by kary on 16/5/30.
 */

public class CubeBuildSingleTable extends AbstractCubeBuild implements CubeBuild {

    private Set<IBusinessPackageGetterService> packs;
    private Set<CubeTableSource> sources;
    private Set<CubeTableSource> allSingleSources;
    private Set<BIBusinessTable> allBusinessTable = new HashSet<BIBusinessTable>();
    private BIUser biUser;
    private Set<List<Set<CubeTableSource>>> dependTableResource;
    private Set<BITableSourceRelation> biTableSourceRelationSet = new HashSet<BITableSourceRelation>();
    private Set<BITableSourceRelationPath> biTableSourceRelationPathSet = new HashSet<BITableSourceRelationPath>();
    private Set<BICubeGenerateRelationPath> cubeGenerateRelationPathSet = new HashSet<BICubeGenerateRelationPath>();
    private Set<BICubeGenerateRelation> cubeGenerateRelationSet = new HashSet<BICubeGenerateRelation>();
    private Set<BITableRelation> inUseRelations = new HashSet<BITableRelation>();
    private Set<BITableRelationPath> inUsePaths = new HashSet<BITableRelationPath>();
    private int updateType = DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;
    private CubeTableSource childTableSource;

    public CubeBuildSingleTable(BusinessTable hostTable, String childTableSourceId, long userId, int updateType) {
        super(userId);
        this.biUser = new BIUser(userId);
        this.updateType = updateType;
        init(hostTable, childTableSourceId);
    }

    public void init(BusinessTable businessTable, String childTableSourceId) {

        try {
            setAllSources(businessTable);
            Set<List<Set<CubeTableSource>>> depends = calculateTableSource(getSources());
            setDependTableResource(depends);
            setAllSingleSources(set2Set(depends));
            setChildTableSource(childTableSourceId);
            calculateRelationsAndPaths(businessTable);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private void setChildTableSource(String childTableSourceId) {
        if (null != childTableSourceId) {
            for (CubeTableSource source : this.getAllSingleSources()) {
                if (ComparatorUtils.equals(source.getSourceID(), childTableSourceId)) {
                    this.childTableSource = source;
                }
            }
        }
    }

    private void calculateRelationsAndPaths(BusinessTable businessTable) {
        //获取生成过的关联，没生成的不管
        Set<BITableRelation> generatedRelations = getGeneratedRelations();
        Set<BITableRelationPath> generatedPaths = getGeneratedPaths(generatedRelations);
        //遍历所有路径，能链到该表的关联（路径）都要被更新
        calculateAllRelationsAndPaths(businessTable, generatedRelations, generatedPaths);
        //设置路径（关联）的依赖关系
        setCubeGenerateRelationSet(inUseRelations, businessTable);
        setCubeGenerateRelationPathSet(inUsePaths);
    }

    private void calculateAllRelationsAndPaths(BusinessTable businessTable, Set<BITableRelation> generatedRelations, Set<BITableRelationPath> generatedPaths) {
        for (BITableRelation tableRelation : generatedRelations) {
            if (tableRelation.getPrimaryTable().getID().getIdentity().equals(businessTable.getID().getIdentity()) || tableRelation.getForeignTable().getID().getIdentity().equals(businessTable.getID().getIdentity())) {
                inUseRelations.add(tableRelation);
            }
        }
        for (BITableRelationPath path : generatedPaths) {
            for (BITableRelation tableRelation : path.getAllRelations()) {
                if (inUseRelations.contains(tableRelation)) {
                    inUsePaths.add(path);
                    for (BITableRelation biTableRelation : path.getAllRelations()) {
                        inUseRelations.add(biTableRelation);
                    }
                    break;
                }
            }

        }
    }

    public void setCubeGenerateRelationSet(Set<BITableRelation> inUseRelations, BusinessTable businessTable) {
        for (BITableRelation tableRelation : inUseRelations) {
            if (isTableRelationValid(tableRelation)) {
                BITableRelation tempTableRelation = new BITableRelation(tableRelation.getPrimaryField(), tableRelation.getForeignField());
                BITableSourceRelation convertRelation = convertRelation(tempTableRelation);
                if (null != convertRelation) {
                    this.biTableSourceRelationSet.add(convertRelation);
                    Set<CubeTableSource> dependTableSourceSet = new HashSet<CubeTableSource>();
                    boolean containsTable = convertRelation.getPrimaryTable().getSourceID().equals(BusinessTableHelper.getTableDataSource(businessTable.getID()).getSourceID()) || convertRelation.getForeignTable().getSourceID().equals(BusinessTableHelper.getTableDataSource(businessTable.getID()).getSourceID());
                    if (containsTable) {
                        dependTableSourceSet.add(BusinessTableHelper.getTableDataSource(businessTable.getID()));
                    }
                    BICubeGenerateRelation generateRelation = new BICubeGenerateRelation(convertRelation, dependTableSourceSet);
                    this.cubeGenerateRelationSet.add(generateRelation);
                }
            }
        }

    }

    public void setCubeGenerateRelationPathSet(Set<BITableRelationPath> inUsePaths) {
        for (BITableRelationPath path : inUsePaths) {
            try {
                this.biTableSourceRelationPathSet.add(convertPath(path));
                this.cubeGenerateRelationPathSet.add(new BICubeGenerateRelationPath(convertPath(path)));
            } catch (BITablePathConfusionException e) {
                BILogger.getLogger().error(e.getMessage());
            }
        }

    }

    private Set<BITableRelationPath> getGeneratedPaths(Set<BITableRelation> generatedRelations) {
        Set<BITableRelationPath> generatedRelationPaths = new HashSet<BITableRelationPath>();
        for (BITableRelationPath tableRelationPath : allRelationPathSet) {
            boolean flag = true;
            if (tableRelationPath.size() == BIRelationUtils.PATH_NULL || tableRelationPath.size() == BIRelationUtils.PATH_RELATION) {
                flag = false;
            }
            for (BITableRelation tableRelation : tableRelationPath.getAllRelations()) {
                if (!generatedRelations.contains(tableRelation)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                generatedRelationPaths.add(tableRelationPath);
            }
        }
        return generatedRelationPaths;
    }

    private Set<BITableRelation> getGeneratedRelations() {
        BITableRelationConfigurationProvider relationManager = BICubeConfigureCenter.getTableRelationManager();
        Set<BITableRelation> generatedRelations = new HashSet<BITableRelation>();
        for (BITableRelation relation : relationManager.getAllTableRelation(biUser.getUserId())) {
            try {
                if (relationManager.isRelationGenerated(biUser.getUserId(), relation)) {
                    generatedRelations.add(relation);
                }
            } catch (BITableAbsentException e) {
                BILogger.getLogger().error(e.getMessage());
            } catch (BIRelationAbsentException e) {
                BILogger.getLogger().error(e.getMessage());
            }
        }
        return generatedRelations;
    }

    private void setAllSources(BusinessTable businessTable) {
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(biUser.getUserId());
        this.packs = packs;
        this.sources = new HashSet<CubeTableSource>();
        allBusinessTable = new HashSet<BIBusinessTable>();
        for (IBusinessPackageGetterService pack : packs) {
            Iterator<BIBusinessTable> tIt = pack.getBusinessTables().iterator();
            while (tIt.hasNext()) {
                BIBusinessTable table = tIt.next();
                if (ComparatorUtils.equals(table.getID(), businessTable.getID())) {
                    allBusinessTable.add(table);
                    sources.add(table.getTableSource());
                }
            }
        }
    }

    /**
     * @return the packs
     */
    public Set<IBusinessPackageGetterService> getPacks() {
        return packs;
    }

    /**
     * @return sources
     */
    public Set<CubeTableSource> getSources() {
        return sources;
    }


    public Set<BITableSourceRelationPath> getBiTableSourceRelationPathSet() {
        return biTableSourceRelationPathSet;
    }


    @Override
    public Set<CubeTableSource> getAllSingleSources() {
        return allSingleSources;
    }

    public void setAllSingleSources(Set<CubeTableSource> allSingleSources) {
        this.allSingleSources = allSingleSources;
    }


    public Set<List<Set<CubeTableSource>>> getDependTableResource() {
        return dependTableResource;
    }

    @Override
    public Set<BITableRelation> getTableRelationSet() {
        return inUseRelations;
    }

    @Override
    public Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet() {
        return this.cubeGenerateRelationPathSet;
    }

    @Override
    public Set<BICubeGenerateRelation> getCubeGenerateRelationSet() {
        return this.cubeGenerateRelationSet;
    }

    @Override
    public boolean preConditionsCheck() {
        return true;
    }

    @Override
    public boolean isSingleTable() {
        return true;
    }

    public void setDependTableResource(Set<List<Set<CubeTableSource>>> dependTableResource) {
        this.dependTableResource = dependTableResource;
    }

    @Override
    public Map<CubeTableSource, UpdateSettingSource> getUpdateSettingSources() {
        Map<CubeTableSource, UpdateSettingSource> map = new HashMap<CubeTableSource, UpdateSettingSource>();
        if (null == childTableSource) {
            return map;
        }
        for (CubeTableSource source : this.getAllSingleSources()) {
            UpdateSettingSource updateSettingSource = BIConfigureManagerCenter.getUpdateFrequencyManager().getTableUpdateSetting(source.getSourceID(), biUser.getUserId());
            if (null != updateSettingSource) {
                if (ComparatorUtils.equals(source.getSourceID(), this.childTableSource.getSourceID())) {
                    updateSettingSource.setUpdateType(updateType);
                } else {
                    updateSettingSource.setUpdateType(DBConstant.SINGLE_TABLE_UPDATE_TYPE.NEVER);
                }
            } else {
                updateSettingSource = new UpdateSettingSource();
                updateSettingSource.setUpdateType(DBConstant.SINGLE_TABLE_UPDATE_TYPE.NEVER);
            }
            map.put(source, updateSettingSource);
        }
        return map;
    }

    /**
     * @return the tableSourceRelationSet
     */
    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return this.biTableSourceRelationSet;
    }

    @Override
    public boolean copyFileFromOldCubes() {
        try {
            ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(String.valueOf(biUser.getUserId()));
            ICubeConfiguration advancedConf = BICubeConfiguration.getConf(String.valueOf(biUser.getUserId()));
            BICubeResourceRetrieval tempResourceRetrieval = new BICubeResourceRetrieval(tempConf);
            BICubeResourceRetrieval advancedResourceRetrieval = new BICubeResourceRetrieval(advancedConf);
            if (new File(tempConf.getRootURI().getPath()).exists()) {
                BIFileUtils.delete(new File(tempConf.getRootURI().getPath()));
            }
            new File(tempConf.getRootURI().getPath()).mkdirs();
            Set<CubeTableSource> tableSet = new HashSet<CubeTableSource>();
            for (BICubeGenerateRelation relation : this.getCubeGenerateRelationSet()) {
                tableSet.add(relation.getRelation().getPrimaryTable());
                tableSet.add(relation.getRelation().getForeignTable());
            }
            tableSet = set2Set(calculateTableSource(tableSet));
            for (CubeTableSource source : tableSet) {
                copyFilesFromOldCubes(tempResourceRetrieval, advancedResourceRetrieval, source);
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage());
        }
        return true;
    }

    private void copyFilesFromOldCubes(ICubeResourceRetrievalService tempResourceRetrieval, ICubeResourceRetrievalService advancedResourceRetrieval, CubeTableSource source) throws BICubeResourceAbsentException, BITablePathEmptyException, IOException {
        ICubeResourceLocation from = advancedResourceRetrieval.retrieveResource(new BITableKey(source));
        ICubeResourceLocation to = tempResourceRetrieval.retrieveResource(new BITableKey(source));
        BIFileUtils.copyFolder(new File(from.getAbsolutePath()), new File(to.getAbsolutePath()));
    }

    @Override
    public boolean replaceOldCubes() {
        ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(String.valueOf(biUser.getUserId()));
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(String.valueOf(biUser.getUserId()));
        try {
            BIFileUtils.moveFile(tempConf.getRootURI().getPath().toString(), advancedConf.getRootURI().getPath().toString());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage());
        }
        return true;
    }


    /**
     * TODO改变层级结构
     *
     * @param set
     * @return
     */
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

}
