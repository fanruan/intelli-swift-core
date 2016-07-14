package com.finebi.cube.conf;

import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 * 为relation和path计算依赖
 */
public interface CalculateDependTool {
    BICubeGenerateRelation calRelations(BITableSourceRelation biTableSourceRelation,Set<CubeTableSource> cubeTableSources);

    BICubeGenerateRelationPath calRelationPath(BITableSourceRelationPath biTableSourceRelationPathSet, Set<BITableSourceRelation> tableRelationSet);

}

