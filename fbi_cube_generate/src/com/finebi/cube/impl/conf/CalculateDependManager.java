package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.CalculateDepend;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
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
        BICubeGenerateRelationPath biCubeGenerateRelationPath = null;

        biCubeGenerateRelationPath = new BICubeGenerateRelationPath(biTableSourceRelationPath, null);
        return biCubeGenerateRelationPath;
    }
}
