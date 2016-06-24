package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.CalculateDependTool;
import com.finebi.cube.relation.*;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public class CalculateDependManagerTest extends TestCase {
    private CalculateDependTool calculateDependManager4Test;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        calculateDependManager4Test = new CalculateDependManager4Test();

    }


    public void testRelation() {
        addTableABC();
        BITableSourceRelation biTableSourceRelation = BITableSourceRelationTestTool.getMemoryBC();
        BICubeGenerateRelation biTableSourceRelation4CubeGenerate = calculateDependManager4Test.calRelations(biTableSourceRelation);
        assertTrue(biTableSourceRelation4CubeGenerate.getDependTableSourceSet().contains((BIMemoryDataSourceFactory.generateTableC())));
        assertTrue(biTableSourceRelation4CubeGenerate.getDependTableSourceSet().contains((BIMemoryDataSourceFactory.generateTableB())));
    }

    public void testRelationPath() {
        addTableABC();
        BITableSourceRelationPath abcPath = BITableSourceRelationPathTestTool.getABCPath();
        Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
        relations.add(BITableSourceRelationTestTool.getMemoryBC());
        BICubeGenerateRelationPath biTableRelationPath4CubeGenerate = calculateDependManager4Test.calRelationPath(abcPath, relations);
        assertTrue(biTableRelationPath4CubeGenerate.getDependRelationPathSet().size() == 2);
        assertTrue(biTableRelationPath4CubeGenerate.getBiTableSourceRelationPath().getSourceID().equals(BITableSourceRelationPathTestTool.getABCPath().getSourceID()));
    }

    /*假设A表已经生成*/
    public void testRelation4Incremental() {
        addTableC();
        BITableSourceRelation biTableSourceRelation = BITableSourceRelationTestTool.getMemoryBC();
        BICubeGenerateRelation biTableSourceRelation4CubeGenerate = calculateDependManager4Test.calRelations(biTableSourceRelation);
        assertTrue(biTableSourceRelation4CubeGenerate.getDependTableSourceSet().contains((BIMemoryDataSourceFactory.generateTableC())));
        assertTrue(biTableSourceRelation4CubeGenerate.getDependTableSourceSet().size() == 1);
    }

    public void testRelationPathIncremental() {
        addTableC();
        BITableSourceRelationPath abcPath = BITableSourceRelationPathTestTool.getABCPath();
        Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
        relations.add(BITableSourceRelationTestTool.getMemoryBC());
        BICubeGenerateRelationPath biTableRelationPath4CubeGenerate = calculateDependManager4Test.calRelationPath(abcPath, relations);
        assertTrue(biTableRelationPath4CubeGenerate.getDependRelationPathSet().size() == 2);
        try {
            Set<String> ids=new HashSet<String>();
            for (BITableSourceRelationPath biTableSourceRelationPath : biTableRelationPath4CubeGenerate.getDependRelationPathSet()) {
                ids.add(biTableSourceRelationPath.getSourceID());
            }
            ;
            BITableSourceRelationPath pathCopy=new BITableSourceRelationPath();
            pathCopy.copyFrom(biTableRelationPath4CubeGenerate.getBiTableSourceRelationPath());
            pathCopy.removeLastRelation();
            assertTrue(ids.contains(new BITableSourceRelationPath(abcPath.getLastRelation()).getSourceID()));
            assertTrue(ids.contains(pathCopy.getSourceID()));
        } catch (BITablePathEmptyException e) {
            assertFalse(true);
        }
    }

    private void addTableC() {
        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
        cubeTableSourceSet.add(BIMemoryDataSourceFactory.generateTableC());
        calculateDependManager4Test.setOriginal(cubeTableSourceSet);
    }

    private void addTableABC() {
        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
        cubeTableSourceSet.add(BIMemoryDataSourceFactory.generateTableA());
        cubeTableSourceSet.add(BIMemoryDataSourceFactory.generateTableB());
        cubeTableSourceSet.add(BIMemoryDataSourceFactory.generateTableC());
        calculateDependManager4Test.setOriginal(cubeTableSourceSet);

    }
    
}
