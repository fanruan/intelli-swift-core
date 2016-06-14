package com.finebi.cube.conf;

import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelation4Incremental;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.relation.BITableSourceRelationPath4Incremetal;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 * 为relation和path计算依赖
 */
public interface CalculateDepend {
    BITableSourceRelation4Incremental calRelations(BITableSourceRelation biTableSourceRelationSet);

    BITableSourceRelationPath4Incremetal calRelationPath(BITableSourceRelationPath biTableSourceRelationPathSet, Set<BITableSourceRelation> tableRelationSet);

    void setOriginal(Set<CubeTableSource> cubeTableSources);
}

