package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.AbstractCubeBuildStuff;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CalculateDependTool;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.utils.BIDataStructTranUtils;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 * kary 这个是真正意义上完整的全局更新，无论是否有数据，更新所有能更新的
 */
public class CubeBuildStuffComplete extends AbstractCubeBuildStuff implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2315016175890907748L;
    private Set<CubeTableSource> allSingleSources;

    private static BILogger LOGGER = BILoggerFactory.getLogger(CubeBuildStuffComplete.class);

    private Set<BITableSourceRelation> tableSourceRelationSet;
    private Set<BITableRelation> tableRelationSet;
    private Map<CubeTableSource, Set<BITableSourceRelation>> primaryKeyMap;
    private Map<CubeTableSource, Set<BITableSourceRelation>> foreignKeyMap;
    private BIUser biUser;
    private Set<BITableSourceRelationPath> relationPaths;
    private Set<BICubeGenerateRelationPath> cubeGenerateRelationPathSet;
    private Set<BICubeGenerateRelation> cubeGenerateRelationSet;
    /**
     * TableSource之间存在依赖关系，这一点很合理。
     * 这个结构肯定是不好的。
     * 不合理的在于为何要把这个依赖关系用一个List(原来是个Map)，把间接依赖的统统获得。
     * 开发的时候封装一下即可，如果当时不封装，这个结构就镶嵌代码了，随着开发替换代价越高。
     */
    private Set<List<Set<CubeTableSource>>> dependTableResource;


    public CubeBuildStuffComplete(BIUser biUser) {
        super(biUser.getUserId());
        this.biUser = biUser;
        initialCubeStuff();
    }


    public Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet() {
        return this.cubeGenerateRelationPathSet;
    }

    public Set<BICubeGenerateRelation> getCubeGenerateRelationSet() {
        return this.cubeGenerateRelationSet;
    }

    private Set<BITableRelation> filterRelation(Set<BITableRelation> tableRelationSet) {
        Iterator<BITableRelation> iterator = tableRelationSet.iterator();
        Set<BITableRelation> result = new HashSet<BITableRelation>();
        while (iterator.hasNext()) {
            BITableRelation tableRelation = iterator.next();
            if (configHelper.isTableRelationValid(tableRelation)) {
                result.add(tableRelation);
            }
        }
        return tableRelationSet;
    }

    public void setTableRelationSet(Set<BITableRelation> tableRelationSet) {
        this.tableRelationSet = filterRelation(tableRelationSet);
        this.tableSourceRelationSet = filterRelations(convertRelations(this.tableRelationSet));
    }

    public Set<BITableSourceRelationPath> getTableSourceRelationPathSet() {
        return relationPaths;
    }

    public void setRelationPaths(Set<BITableSourceRelationPath> relationPaths) {
        this.relationPaths = relationPaths;
    }

    private Set<BITableSourceRelation> convertRelations(Set<BITableRelation> relationSet) {
        Set<BITableSourceRelation> set = new HashSet<BITableSourceRelation>();
        for (BITableRelation relation : relationSet) {
            try {
                BITableSourceRelation tableSourceRelation = configHelper.convertRelation(relation);
                if (null != tableSourceRelation) {
                    set.add(tableSourceRelation);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                continue;
            }
        }
        return set;
    }


    private Set<BITableSourceRelationPath> convertPaths(Set<BITableRelationPath> paths) {
        Set<BITableSourceRelationPath> set = new HashSet<BITableSourceRelationPath>();
        for (BITableRelationPath path : paths) {
            try {
                BITableSourceRelationPath relationPath = convertPath(path);
                if (null != relationPath) {
                    set.add(relationPath);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        set = removeDuplicateRelationPaths(set);
        return set;
    }

    @Override
    public Set<CubeTableSource> getSingleSourceLayers() {
        BIConfigureManagerCenter.getLogManager().cubeTableSourceSet(allSingleSources, biUser.getUserId());
        return allSingleSources;
    }

    @Override
    public boolean copyFileFromOldCubes() {
        ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(String.valueOf(biUser.getUserId()));
        if (new File(tempConf.getRootURI().getPath()).exists()) {
            BIFileUtils.delete(new File(tempConf.getRootURI().getPath()));
        }
        BICubeConfiguration advancedTempConf = BICubeConfiguration.getAdvancedTempConf(String.valueOf(biUser.getUserId()));
        if (new File(advancedTempConf.getRootURI().getPath()).exists()) {
            BIFileUtils.delete(new File(tempConf.getRootURI().getPath()));
        }
        return true;
    }

    @Override
    public Map<CubeTableSource, UpdateSettingSource> getUpdateSettingSources() {
        Map<CubeTableSource, UpdateSettingSource> updateSettingSourceMap = new HashMap<CubeTableSource, UpdateSettingSource>();
        for (CubeTableSource source : allSingleSources) {
            updateSettingSourceMap.put(source, setUpdateTypes(source));
        }
        return updateSettingSourceMap;
    }

    public String getCubeTaskId() {
        return DBConstant.GLOBAL_UPDATE_TYPE.COMPLETE_UPDATE;
    }

    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.ALL;
    }

    @Override
    public Set<String> getTaskTableSourceIds() {
        if (taskTableSourceIDs == null) {
            taskTableSourceIDs = getDependTableSourceIdSet(dependTableResource);
        }
        return taskTableSourceIDs;
    }

    public void setAllSingleSources(Set<CubeTableSource> allSingleSources) {
        this.allSingleSources = allSingleSources;
    }


    public Set<List<Set<CubeTableSource>>> getDependTableResource() {
        return dependTableResource;
    }

    public void setDependTableResource(Set<List<Set<CubeTableSource>>> dependTableResource) {
        this.dependTableResource = dependTableResource;
    }


    /**
     * @return the tableSourceRelationSet
     */
    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return tableSourceRelationSet;
    }

    /**
     * @return the primaryKeyMap
     */
    public Map<CubeTableSource, Set<BITableSourceRelation>> getPrimaryKeyMap() {
        return primaryKeyMap;
    }

    /**
     * @param primaryKeyMap the primaryKeyMap to set
     */
    public void setPrimaryKeyMap(Map<CubeTableSource, Set<BITableSourceRelation>> primaryKeyMap) {
        this.primaryKeyMap = primaryKeyMap;
    }

    /**
     * @return the foreignKeyMap
     */
    public Map<CubeTableSource, Set<BITableSourceRelation>> getForeignKeyMap() {
        return foreignKeyMap;
    }

    /**
     * @param foreignKeyMap the foreignKeyMap to set
     */
    public void setForeignKeyMap(Map<CubeTableSource, Set<BITableSourceRelation>> foreignKeyMap) {
        this.foreignKeyMap = foreignKeyMap;
    }

    public void initialCubeStuff() {
        try {
            Set<List<Set<CubeTableSource>>> depends = calculateTableSource(getSystemTableSources());
            setDependTableResource(depends);
            setAllSingleSources(BIDataStructTranUtils.set2Set(depends));
            setTableRelationSet(BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(biUser.getUserId()));
            Map<CubeTableSource, Set<BITableSourceRelation>> primaryKeyMap = new HashMap<CubeTableSource, Set<BITableSourceRelation>>();
            setPrimaryKeyMap(primaryKeyMap);
            Map<CubeTableSource, Set<BITableSourceRelation>> foreignKeyMap = new HashMap<CubeTableSource, Set<BITableSourceRelation>>();
            setForeignKeyMap(foreignKeyMap);
            Set<BITableRelationPath> allPath = BICubeConfigureCenter.getTableRelationManager().getAllTablePath(biUser.getUserId());
            Set<BITableRelationPath> filteredPath = new HashSet<BITableRelationPath>();
            for (BITableRelationPath path : allPath) {
                if (path.size() > 1) {
                    filteredPath.add(path);
                }
            }
            setRelationPaths(convertPaths(filteredPath));
            calculateDepend();
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private void calculateDepend() {
        CalculateDependTool cal = new CalculateDependManager();
        cubeGenerateRelationSet = new HashSet<BICubeGenerateRelation>();
        for (BITableSourceRelation biTableSourceRelation : this.getTableSourceRelationSet()) {
            this.cubeGenerateRelationSet.add(cal.calRelations(biTableSourceRelation, this.getSystemTableSources()));
        }
        cubeGenerateRelationPathSet = new HashSet<BICubeGenerateRelationPath>();
        cubeGenerateRelationPathSet = cal.calRelationPath(this.getTableSourceRelationPathSet(), this.tableSourceRelationSet);
    }

}
