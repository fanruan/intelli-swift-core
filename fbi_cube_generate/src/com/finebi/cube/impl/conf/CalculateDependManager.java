package com.finebi.cube.impl.conf;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.CalculateDependTool;
import com.finebi.cube.gen.oper.BIRelationIDUtils;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;

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
    public Set<BICubeGenerateRelationPath> calRelationPath(Set<BITableSourceRelationPath> pathsBuilt, Set<BITableSourceRelation> relationsBuilt) {
        Set<BICubeGenerateRelationPath> cubeGenerateRelationPathSet = new HashSet<BICubeGenerateRelationPath>();
        Set<String> pathIDs = new HashSet<String>();
        for (BITableSourceRelationPath path : pathsBuilt) {
            pathIDs.add(BIRelationIDUtils.calculatePathID(path));
        }
        Set<String> relationIDs = new HashSet<String>();
        for (BITableSourceRelation relation : relationsBuilt) {
            relationIDs.add(BIRelationIDUtils.calculateRelationID(relation));
        }
        for (BITableSourceRelationPath path : pathsBuilt) {
            if (path.getAllRelations().size() < 2) {
                continue;
            }
            Set<BITableSourceRelationPath> pathDepends = new HashSet<BITableSourceRelationPath>();
            try {
                if (relationIDs.contains(BIRelationIDUtils.calculateRelationID(path.getLastRelation()))) {
                    pathDepends.add(new BITableSourceRelationPath(path.getLastRelation()));
                }

                BITableSourceRelationPath pathFront = new BITableSourceRelationPath();
                pathFront.copyFrom(path);
                pathFront.removeLastRelation();
                if (pathFront.getAllRelations().size() == 1) {
                    if (relationIDs.contains(BIRelationIDUtils.calculateRelationID(pathFront.getFirstRelation()))) {
                        pathDepends.add(pathFront);
                    }
                }
                if (pathFront.getAllRelations().size() >= 2) {
                    if (pathIDs.contains(BIRelationIDUtils.calculatePathID(pathFront))) {
                        pathDepends.add(pathFront);
                    }
                }
            } catch (BITablePathEmptyException e) {
                BILoggerFactory.getLogger().error(e.getMessage());
            }
            BICubeGenerateRelationPath biCubeGenerateRelationPath = new BICubeGenerateRelationPath(path, pathDepends);
            cubeGenerateRelationPathSet.add(biCubeGenerateRelationPath);
        }
        return cubeGenerateRelationPathSet;
    }
}
