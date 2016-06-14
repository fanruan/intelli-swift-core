package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.CalculateDepend;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelation4Incremental;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.relation.BITableSourceRelationPath4Incremetal;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.fr.bi.stable.data.source.CubeTableSource;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public class CalculateDependManagerTest extends TestCase {
    private CalculateDepend calculateDependManager4Test;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        calculateDependManager4Test = new CalculateDependManager4Test();
        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
        cubeTableSourceSet.add(BIMemoryDataSourceFactory.generateTableC());
        calculateDependManager4Test.setOriginal(cubeTableSourceSet);
    }

    public void testRelation() {
        BITableSourceRelation biTableSourceRelation = BITableSourceRelationTestTool.getMemoryBC();
        BITableSourceRelation4Incremental biTableSourceRelation4Incrementals = calculateDependManager4Test.calRelations(biTableSourceRelation);
        checkRelation(biTableSourceRelation4Incrementals);
    }

    public void testRealationPath() {
        BITableSourceRelationPath abcPath = BITableSourceRelationPathTestTool.getABCPath();
        Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
        relations.add(BITableSourceRelationTestTool.getMemoryBC());
        BITableSourceRelationPath4Incremetal biTableSourceRelationPath4Incremetal = calculateDependManager4Test.calRelationPath(abcPath, relations);
        checkPath(biTableSourceRelationPath4Incremetal, relations);
    }

    private void checkPath(BITableSourceRelationPath4Incremetal biTableSourceRelationPath4Incremetal, Set<BITableSourceRelation> relations) {
        assertTrue(biTableSourceRelationPath4Incremetal.getBiTableSourceRelation().size() == relations.size());
    }

    private void checkRelation(BITableSourceRelation4Incremental biTableSourceRelation4Incremental) {
        assertTrue(biTableSourceRelation4Incremental.getCubeTableSourceSet().contains((BIMemoryDataSourceFactory.generateTableC().getSourceID())));
    }
}