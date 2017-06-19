package com.fr.bi.cal.generate.task.calculator;

import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.ICubeGenerateTask;
import com.finebi.cube.conf.ITaskCalculator;
import com.finebi.cube.impl.conf.CubeBuildStuffEmptyTable;
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
public class EmptyTaskCalculator implements ITaskCalculator {

    private ICubeGenerateTask cubeGenerateTask;

    public EmptyTaskCalculator(ICubeGenerateTask cubeGenerateTask) {
        this.cubeGenerateTask = cubeGenerateTask;
    }

    @Override
    public CubeBuildStuff generateCubeBuildStuff(Set<CubeTableSource> allTableSources,
                                                 Set<BITableSourceRelation> allRelations, Set<BITableSourceRelationPath> allPaths) {
        return new CubeBuildStuffEmptyTable(cubeGenerateTask.getUserId(), allTableSources);
    }
}
