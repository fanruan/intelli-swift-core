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
public class CalculateDependManager implements CalculateDependTool {

    @Override
    public BICubeGenerateRelation calRelations(BITableSourceRelation biTableSourceRelation, Set<CubeTableSource> cubeTableSources) {
        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
        if (cubeTableSources.contains(biTableSourceRelation.getForeignTable())) {
            cubeTableSourceSet.add(biTableSourceRelation.getForeignTable());
        }
        if (cubeTableSources.contains(biTableSourceRelation.getPrimaryTable())) {
            cubeTableSourceSet.add(biTableSourceRelation.getPrimaryTable());
        }
        return new BICubeGenerateRelation(biTableSourceRelation, cubeTableSourceSet);
    }

    @Override
    public Set<BICubeGenerateRelationPath> calRelationPath(Set<BITableSourceRelationPath> biTableSourceRelationPathSet, Set<BITableSourceRelation> tableRelationSet) {
        Set<BICubeGenerateRelationPath> cubeGenerateRelationPathSet = new HashSet<BICubeGenerateRelationPath>();
        for (BITableSourceRelationPath biTableSourceRelationPath : biTableSourceRelationPathSet) {
            if (biTableSourceRelationPath.getAllRelations().size() < 2) {
                continue;
            }
            Set<BITableSourceRelationPath> dependRelationPathSet = new HashSet<BITableSourceRelationPath>();
            try {
                if (tableRelationSet.contains(biTableSourceRelationPath.getLastRelation())) {
                    dependRelationPathSet.add(new BITableSourceRelationPath(biTableSourceRelationPath.getLastRelation()));
                }
                BITableSourceRelationPath copyPath = new BITableSourceRelationPath();
                copyPath.copyFrom(biTableSourceRelationPath);
                copyPath.removeLastRelation();
                if (copyPath.getAllRelations().size() == 1) {
                    if (tableRelationSet.contains(copyPath.getFirstRelation())) {
                        dependRelationPathSet.add(copyPath);
                    }
                }
                if (copyPath.getAllRelations().size() >= 2) {
                    for (BITableSourceRelationPath path : biTableSourceRelationPathSet) {
                        if (copyPath.getSourceID().equals(path.getSourceID())){
                            dependRelationPathSet.add(copyPath);
                            break;
                        }
                    }
                }
            } catch (BITablePathEmptyException e) {
                BILogger.getLogger().error(e.getMessage());
            }
            BICubeGenerateRelationPath biCubeGenerateRelationPath = new BICubeGenerateRelationPath(biTableSourceRelationPath, dependRelationPathSet);
            cubeGenerateRelationPathSet.add(biCubeGenerateRelationPath);
        }
        return cubeGenerateRelationPathSet;
    }
}
