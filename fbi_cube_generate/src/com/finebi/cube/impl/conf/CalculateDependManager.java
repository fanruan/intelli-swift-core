package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.CalculateDependTool;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public  class CalculateDependManager implements CalculateDependTool {
    public Set<CubeTableSource> analysisTableSources;

    @Override
    public BICubeGenerateRelation calRelations(BITableSourceRelation biTableSourceRelation) {
        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
        if (analysisTableSources.contains(biTableSourceRelation.getForeignTable())) {
            cubeTableSourceSet.add(biTableSourceRelation.getForeignTable());
        }
        if (analysisTableSources.contains(biTableSourceRelation.getPrimaryTable())) {
            cubeTableSourceSet.add(biTableSourceRelation.getPrimaryTable());
        }
        return new BICubeGenerateRelation(biTableSourceRelation, cubeTableSourceSet);
    }

    @Override
    public BICubeGenerateRelationPath calRelationPath(BITableSourceRelationPath biTableSourceRelationPath, Set<BITableSourceRelation> tableRelationSet) {
        if (biTableSourceRelationPath.getAllRelations().size()<2){
            return null;
        }
        Set<BITableSourceRelationPath> dependRelationPathSet=new HashSet<BITableSourceRelationPath>();
        try {
            if (tableRelationSet.contains(biTableSourceRelationPath.getLastRelation())) {
                dependRelationPathSet.add(new BITableSourceRelationPath(biTableSourceRelationPath.getLastRelation()));
            }
            BITableSourceRelationPath copyPath=new BITableSourceRelationPath();
            copyPath.copyFrom(biTableSourceRelationPath);
            copyPath.removeLastRelation();
            if(copyPath.getAllRelations().size()>1||tableRelationSet.contains(copyPath.getFirstRelation())) {
                dependRelationPathSet.add(copyPath);
            }
        } catch (BITablePathEmptyException e) {
            BILogger.getLogger().error(e.getMessage());
        }
        BICubeGenerateRelationPath biCubeGenerateRelationPath = new BICubeGenerateRelationPath(biTableSourceRelationPath, dependRelationPathSet);
        return biCubeGenerateRelationPath;
    }
    @Override
    public void setOriginal(Set<CubeTableSource> cubeTableSources) {
        analysisTableSources = cubeTableSources;
    }
}
