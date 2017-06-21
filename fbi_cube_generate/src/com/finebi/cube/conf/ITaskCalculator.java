package com.finebi.cube.conf;

import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by Lucifer on 2017-5-24.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public interface ITaskCalculator {
    CubeBuildStuff generateCubeBuildStuff(Set<CubeTableSource> allTableSources,
                                          Set<BITableSourceRelation> allRelations, Set<BITableSourceRelationPath> allPaths);
}
