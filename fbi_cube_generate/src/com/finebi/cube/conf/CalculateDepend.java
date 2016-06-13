package com.finebi.cube.conf;

import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.*;

import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 * 为relation和path计算依赖
 */
public interface CalculateDepend {
    Set<BITableSourceRelation4Incremental> calRelations(Set<BITableSourceRelation> biTableSourceRelationSet);
    Set<BITableSourceRelationPath4Incremetal> calRelationPath(Set<BITableSourceRelationPath> biTableSourceRelationPathSet, Set<BITableSourceRelation> tableRelationSet);
    void setOriginal(Set<BIBusinessTable> analysisTables);
}

