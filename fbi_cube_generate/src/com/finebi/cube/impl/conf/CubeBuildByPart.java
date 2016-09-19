package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.AbstractCubeBuild;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.*;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.general.DateUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by kary on 2016/6/8.
 * 表的增量更新，尽量减少依赖，最大化提升效率
 * 逻辑如下:确定新增的table及relations,设置相关依赖
 */
public class CubeBuildByPart extends AbstractCubeBuild implements CubeBuild {

    private Set<CubeTableSource> allSingleSources = new HashSet<CubeTableSource>();
    private Set<BIBusinessTable> newTables;
    protected Set<CubeTableSource> newTableSources = new HashSet<CubeTableSource>();
    private Set<BITableRelation> relationSet = new HashSet<BITableRelation>();
    private Set<List<Set<CubeTableSource>>> dependTableResource;
    private Set<BITableSourceRelation> biTableSourceRelationSet = new HashSet<BITableSourceRelation>();
    private Set<BICubeGenerateRelation> cubeGenerateRelationSet = new HashSet<BICubeGenerateRelation>();
    private Set<BITableSourceRelationPath> biTableSourceRelationPathSet = new HashSet<BITableSourceRelationPath>();
    private Set<BICubeGenerateRelationPath> cubeGenerateRelationPathSet = new HashSet<BICubeGenerateRelationPath>();
    private long userId;

    public CubeBuildByPart(long userId, Set<BIBusinessTable> newTables, Set<BITableRelation> newRelations) {
        super(userId);
        this.userId = userId;
        this.relationSet = newRelations;
        this.newTables = newTables;
        try {
            setRelations();
            setResourcesAndDepends();
            setRelationPath();
            calculateRelationDepends();
        } catch (BITableAbsentException e) {
            BILogger.getLogger().error(e.getMessage());
        }
    }


    private void calculateRelationDepends() {
        for (BITableSourceRelation biTableSourceRelation : this.getTableSourceRelationSet()) {
            this.cubeGenerateRelationSet.add(calculateDependTool.calRelations(biTableSourceRelation, this.getAllSingleSources()));
        }
        for (BITableSourceRelationPath biTableSourceRelationPath : this.getBiTableSourceRelationPathSet()) {
            BICubeGenerateRelationPath path = calculateDependTool.calRelationPath(biTableSourceRelationPath, this.biTableSourceRelationSet);
            if (null != path && path.getDependRelationPathSet().size() != 0) {
                cubeGenerateRelationPathSet.add(path);
            }
        }
    }

    protected void setResourcesAndDepends() throws BITableAbsentException {
        for (BIBusinessTable biBusinessTable : newTables) {
            newTableSources.add(biBusinessTable.getTableSource());
            newTableSources.add(biBusinessTable.getTableSource());
        }
        Set<List<Set<CubeTableSource>>> depends = calculateTableSource(newTableSources);
        this.dependTableResource = depends;
        this.allSingleSources = set2Set(depends);
    }

    private void setRelations() {
        Iterator<BITableRelation> iterator = relationSet.iterator();
        while (iterator.hasNext()) {
            BITableRelation relation = iterator.next();
            BITableSourceRelation sourceRelation = convertRelation(relation);
            if (null != sourceRelation) {
                biTableSourceRelationSet.add(sourceRelation);
                newTableSources.add(sourceRelation.getForeignTable());
                newTableSources.add(sourceRelation.getPrimaryTable());
            }
        }
        biTableSourceRelationSet = removeDuplicateRelations(biTableSourceRelationSet);
    }

    private void setRelationPath() {
        for (BITableRelationPath path : allRelationPathSet) {
            try {
                boolean containsRelation = false;
                for (BITableRelation relation : relationSet) {
                    if (path.containsRelation(relation)) {
                        containsRelation = true;
                    }
                }
                if (containsRelation) {
                    BITableSourceRelationPath relationPath = convertPath(path);
                    if (null != relationPath) {
                        biTableSourceRelationPathSet.add(relationPath);
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
        biTableSourceRelationPathSet = removeDuplicateRelationPaths(biTableSourceRelationPathSet);
    }

    @Override
    public boolean copyFileFromOldCubes() {
        try {
            BILogger.getLogger().info("start copy some files");
            Long t = System.currentTimeMillis();
            ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(Long.toString(userId));
            ICubeConfiguration advancedConf = BICubeConfiguration.getConf(Long.toString(userId));
            if (new File(tempConf.getRootURI().getPath()).exists()) {
                BIFileUtils.delete(new File(tempConf.getRootURI().getPath()));
            }
            if (new File(advancedConf.getRootURI().getPath()).exists()) {
                BIFileUtils.copyFolder(new File(advancedConf.getRootURI().getPath()), new File(tempConf.getRootURI().getPath()));
            }
            BILogger.getLogger().info("copy files cost time: " + DateUtils.timeCostFrom(t));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage());
        }
        return true;
    }

    public Set<BITableSourceRelationPath> getBiTableSourceRelationPathSet() {
        return biTableSourceRelationPathSet;
    }


    @Override
    public Set<CubeTableSource> getAllSingleSources() {
        return allSingleSources;
    }

    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return this.biTableSourceRelationSet;
    }

    @Override
    public Set<List<Set<CubeTableSource>>> getDependTableResource() {
        return dependTableResource;
    }

    @Override
    public Set<BITableRelation> getTableRelationSet() {
        return this.relationSet;
    }

    public Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet() {
        return this.cubeGenerateRelationPathSet;
    }

    public Set<BICubeGenerateRelation> getCubeGenerateRelationSet() {
        return this.cubeGenerateRelationSet;
    }

}
