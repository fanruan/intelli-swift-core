package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.CalculateDepend;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelation4Incremental;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.relation.BITableSourceRelationPath4Incremetal;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public abstract class CalculateDependManager implements CalculateDepend {
    public Set<CubeTableSource> analysisTableSources;

    @Override
    public BITableSourceRelation4Incremental calRelations(BITableSourceRelation biTableSourceRelation) {
        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
        if (analysisTableSources.contains(biTableSourceRelation.getForeignTable())) {
            cubeTableSourceSet.add(biTableSourceRelation.getForeignTable());
        }
        if (analysisTableSources.contains(biTableSourceRelation.getPrimaryTable())) {
            cubeTableSourceSet.add(biTableSourceRelation.getPrimaryTable());
        }
        return new BITableSourceRelation4Incremental(biTableSourceRelation, cubeTableSourceSet);
    }

    @Override
    public BITableSourceRelationPath4Incremetal calRelationPath(BITableSourceRelationPath biTableSourceRelationPath, Set<BITableSourceRelation> tableRelationSet) {
        BITableSourceRelationPath4Incremetal biTableSourceRelationPath4Incremetal = null;
        BITableSourceRelationPath pathCopy = new BITableSourceRelationPath();
        pathCopy.copyFrom(biTableSourceRelationPath);
        Set<BITableSourceRelation> biTableSourceRelationSet = new HashSet<BITableSourceRelation>();
        try {
            while (pathCopy.getAllRelations().size() > 0 && tableRelationSet.contains(pathCopy.getFirstRelation())) {
                biTableSourceRelationSet.add(pathCopy.getFirstRelation());
                pathCopy.removeFirstRelation();
            }
            while (pathCopy.getAllRelations().size() > 0 && tableRelationSet.contains(pathCopy.getLastRelation())) {
                biTableSourceRelationSet.add(pathCopy.getLastRelation());
                pathCopy.removeLastRelation();
            }
            biTableSourceRelationPath4Incremetal = new BITableSourceRelationPath4Incremetal(biTableSourceRelationPath, biTableSourceRelationSet);
        } catch (BITablePathEmptyException e) {
            biTableSourceRelationPath4Incremetal = new BITableSourceRelationPath4Incremetal(biTableSourceRelationPath, null);
            BILogger.getLogger().error(e.getMessage());
        }
        return biTableSourceRelationPath4Incremetal;
    }
}
