package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.AbstractCubeBuild;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.*;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;

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

    private Set<CubeTableSource> allSingleSources;
    private Set<BIBusinessTable> newTables;
    protected Set<CubeTableSource> newTableSources ;
    private Set<BITableRelation> newRelations;
    private Set<List<Set<CubeTableSource>>> dependTableResource;
    private Set<BITableRelation> tableRelationSet;
    private Set<BITableSourceRelation> biTableSourceRelationSet;
    private Set<BICubeGenerateRelation> cubeGenerateRelationSet;

    //    private Set<BITableSourceRelationPath> biTableSourceRelationPathSet;
//    private Set<BICubeGenerateRelationPath> cubeGenerateRelationPathSet;

    public CubeBuildByPart(long userId, Set<BIBusinessTable> newTables, Set<BITableRelation> newRelations) {
        super(userId);
        this.newRelations=newRelations;
        this.newTables=newTables;
        init();
        try {
            setRelationAndPath();
            setResourcesAndDepends();
            calculateRelationDepends();
        } catch (BITableAbsentException e) {
            BILogger.getLogger().error(e.getMessage());
        }    }


    private void init() {
        this.sources = new HashSet<CubeTableSource>();
        this.allSingleSources = new HashSet<CubeTableSource>();
        this.tableRelationSet = new HashSet<BITableRelation>();
        this.biTableSourceRelationSet = new HashSet<BITableSourceRelation>();
        cubeGenerateRelationSet = new HashSet<BICubeGenerateRelation>();
        newTableSources = new HashSet<CubeTableSource>();
//        biTableSourceRelationPathSet = new HashSet<BITableSourceRelationPath>();
        
    }


    private void calculateRelationDepends() {
        for (BITableSourceRelation biTableSourceRelation : this.getTableSourceRelationSet()) {
            this.cubeGenerateRelationSet.add(calculateDependTool.calRelations(biTableSourceRelation,this.getAllSingleSources()));
        }
//        cubeGenerateRelationPathSet = new HashSet<BICubeGenerateRelationPath>();
//        for (BITableSourceRelationPath biTableSourceRelationPath : this.getBiTableSourceRelationPathSet()) {
//            BICubeGenerateRelationPath path = cal.calRelationPath(biTableSourceRelationPath, this.biTableSourceRelationSet);
//            if (null != path) {
//                cubeGenerateRelationPathSet.add(path);
//            }
//        }
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

    private void setRelationAndPath() {
        Iterator<BITableRelation> iterator = newRelations.iterator();
        while (iterator.hasNext()){
            BITableRelation relation = iterator.next();
            BITableSourceRelation sourceRelation = convert(relation);
            biTableSourceRelationSet.add(sourceRelation);
            newTableSources.add(sourceRelation.getForeignTable());
            newTableSources.add(sourceRelation.getPrimaryTable());            
        }

    }

    public Set<BITableSourceRelationPath> getBiTableSourceRelationPathSet() {
//        return this.biTableSourceRelationPathSet;
        return new HashSet<BITableSourceRelationPath>();
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
        return new HashSet<BICubeGenerateRelationPath>();
    }

    public Set<BICubeGenerateRelation> getCubeGenerateRelationSet() {
        return this.cubeGenerateRelationSet;
    }

    
}
