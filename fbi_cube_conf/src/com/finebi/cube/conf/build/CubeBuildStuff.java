package com.finebi.cube.conf.build;

import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.List;
import java.util.Set;

/**
 * Created by wuk on 16/5/30.
 */
public interface CubeBuildStuff {
    
    Set<BITableSourceRelationPath> getRelationPaths();

    Set<CubeTableSource> getAllSingleSources();

    Set<BITableSourceRelation> getTableSourceRelationSet();

    Set<CubeTableSource> getSources();

    Set<List<Set<CubeTableSource>>> getDependTableResource();

    Set<BITableRelation> getTableRelationSet();
}
