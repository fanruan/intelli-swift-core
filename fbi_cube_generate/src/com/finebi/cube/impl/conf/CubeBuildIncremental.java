package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.AbstractCubeBuild;
import com.finebi.cube.conf.CalculateDependTool;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.*;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.finebi.cube.conf.BICubeConfigureCenter.getTableRelationManager;

/**
 * Created by kary on 2016/6/8.
 * 表的增量更新，尽量减少依赖，最大化提升效率
 * 逻辑如下:确定新增的table及relations,设置相关依赖
 */
public class CubeBuildIncremental extends AbstractCubeBuild implements CubeBuild {

    private Set<CubeTableSource> allSingleSources;
    private Set<BIBusinessTable> newTables;
    private Set<BITableRelation> newRelations;
    private BIUser biUser;
    private Set<List<Set<CubeTableSource>>> dependTableResource;
    private Set<BITableRelation> tableRelationSet;
    private Set<BITableSourceRelation> biTableSourceRelationSet;
    private Set<BITableSourceRelationPath> biTableSourceRelationPathSet;
    private Set<BICubeGenerateRelationPath> cubeGenerateRelationPathSet;
    private Set<BICubeGenerateRelation> cubeGenerateRelationSet;
    

    public CubeBuildIncremental(long userId, Set<BIBusinessTable> newTables, Set<BITableRelation> newRelations) {
        super(userId);
        this.biUser = new BIUser(userId);
        this.newRelations=newRelations;
        this.newTables=newTables;
        init();
        try {
            setResourcesAndDepends();
            setRelationAndPath();
            calculateRelationDepends();
        } catch (BITableAbsentException e) {
            BILogger.getLogger().error(e.getMessage());
        }    }
    

    private void init() {
        this.sources = new HashSet<CubeTableSource>();
        this.allSingleSources = new HashSet<CubeTableSource>();
        this.tableRelationSet = new HashSet<BITableRelation>();
        biTableSourceRelationSet = new HashSet<BITableSourceRelation>();
        biTableSourceRelationPathSet = new HashSet<BITableSourceRelationPath>();
        
    }


    private void calculateRelationDepends() {
        CalculateDependTool cal = new CalculateDependManager();
        cal.setOriginal(this.getAllSingleSources());
        cubeGenerateRelationSet = new HashSet<BICubeGenerateRelation>();
        for (BITableSourceRelation biTableSourceRelation : this.getTableSourceRelationSet()) {
            this.cubeGenerateRelationSet.add(cal.calRelations(biTableSourceRelation));
        }
        cubeGenerateRelationPathSet = new HashSet<BICubeGenerateRelationPath>();
        for (BITableSourceRelationPath biTableSourceRelationPath : this.getBiTableSourceRelationPathSet()) {
            BICubeGenerateRelationPath path = cal.calRelationPath(biTableSourceRelationPath, this.biTableSourceRelationSet);
            if (null != path) {
                cubeGenerateRelationPathSet.add(path);
            }
        }
    }

    protected void setResourcesAndDepends() throws BITableAbsentException {
        Set<CubeTableSource> cubeTableSourceHashSet = new HashSet<CubeTableSource>();
        for (BIBusinessTable biBusinessTable : newTables) {
            sources.add(biBusinessTable.getTableSource());
            cubeTableSourceHashSet.add(biBusinessTable.getTableSource());
            Set<BITableRelation> primaryRelations = getTableRelationManager().getPrimaryRelation(biUser.getUserId(), biBusinessTable).getContainer();
            Set<BITableRelation> foreignRelations = getTableRelationManager().getForeignRelation(biUser.getUserId(), biBusinessTable).getContainer();
            tableRelationSet.addAll(primaryRelations);
            tableRelationSet.addAll(foreignRelations);
        }
        Set<List<Set<CubeTableSource>>> depends = calculateTableSource(cubeTableSourceHashSet);
        this.dependTableResource = depends;
        this.allSingleSources = set2Set(depends);
    }

    private void setRelationAndPath() {
        try {
            Set<BITableRelationPath> allTablePath = getTableRelationManager().getAllTablePath(biUser.getUserId());
            for (BITableRelationPath biTableRelationPath : allTablePath) {
                for (BITableRelation biTableRelation : tableRelationSet) {
                    if (biTableRelationPath.getAllRelations().contains(biTableRelation)) {
                        biTableSourceRelationPathSet.add(convertBITableRelationPathToBITableSourceRelationPath(biTableRelationPath));
                        break;
                    }
                }
            }
            for (BITableRelation biTableRelation : this.tableRelationSet) {
                BITableSourceRelation biTableSourceRelation = convert(biTableRelation);
                biTableSourceRelationSet.add(biTableSourceRelation);
            }

        } catch (BITableRelationConfusionException e) {
            BILogger.getLogger().error(e.getMessage());
        } catch (BITablePathConfusionException e) {
            BILogger.getLogger().error(e.getMessage());
        }
    }

    

    private BITableSourceRelationPath convertBITableRelationPathToBITableSourceRelationPath(BITableRelationPath path) throws BITablePathConfusionException {
        BITableSourceRelationPath tableSourceRelationPath = new BITableSourceRelationPath();
        for (BITableRelation biTableRelation : path.getAllRelations()) {
            BITableSourceRelation biTableSourceRelation = convert(biTableRelation);
            tableSourceRelationPath.addRelationAtTail(biTableSourceRelation);
        }
        return tableSourceRelationPath;
    }

    public Set<BITableSourceRelationPath> getBiTableSourceRelationPathSet() {
        return this.biTableSourceRelationPathSet;
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
        return this.tableRelationSet;
    }

    public Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet() {
        return this.cubeGenerateRelationPathSet;
    }

    public Set<BICubeGenerateRelation> getCubeGenerateRelationSet() {
        return this.cubeGenerateRelationSet;
    }

    
}
