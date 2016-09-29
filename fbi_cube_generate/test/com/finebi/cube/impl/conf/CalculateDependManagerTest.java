package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.CalculateDependTool;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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
        Set<CubeTableSource> tableABC = addTableABC();
        BITableSourceRelation biTableSourceRelation = BITableSourceRelationTestTool.getMemoryBC();
        BICubeGenerateRelation biTableSourceRelation4CubeGenerate = calculateDependManager4Test.calRelations(biTableSourceRelation, tableABC);
        assertTrue(biTableSourceRelation4CubeGenerate.getDependTableSourceSet().contains((BIMemoryDataSourceFactory.generateTableC())));
        assertTrue(biTableSourceRelation4CubeGenerate.getDependTableSourceSet().contains((BIMemoryDataSourceFactory.generateTableB())));
    }

    public void testRelationPath() {
        BITableSourceRelationPath abcPath = BITableSourceRelationPathTestTool.getABCPath();
        Set<BITableSourceRelationPath> abcPathSet = new HashSet<BITableSourceRelationPath>();
        abcPathSet.add(abcPath);
        Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
        relations.add(BITableSourceRelationTestTool.getMemoryBC());
        Set<BICubeGenerateRelationPath> biTableRelationPath4CubeGenerate = calculateDependManager4Test.calRelationPath(abcPathSet, relations);
        Iterator<BICubeGenerateRelationPath> iterator = biTableRelationPath4CubeGenerate.iterator();
        while (iterator.hasNext()){
            BICubeGenerateRelationPath path = iterator.next();
            assertTrue(path.getDependRelationPathSet().size() == 1);
            assertTrue(path.getBiTableSourceRelationPath().getSourceID().equals(BITableSourceRelationPathTestTool.getABCPath().getSourceID()));
        }
    }

    /*假设A表已经生成*/
    public void testRelation4Part() {
        Set<CubeTableSource> tableC = addTableC();
        BITableSourceRelation biTableSourceRelation = BITableSourceRelationTestTool.getMemoryBC();
        BICubeGenerateRelation biTableSourceRelation4CubeGenerate = calculateDependManager4Test.calRelations(biTableSourceRelation, tableC);
        assertTrue(biTableSourceRelation4CubeGenerate.getDependTableSourceSet().contains((BIMemoryDataSourceFactory.generateTableC())));
        assertTrue(biTableSourceRelation4CubeGenerate.getDependTableSourceSet().size() == 1);
    }

    public void testRelationPath4Part() {
        BITableSourceRelationPath abcPath = BITableSourceRelationPathTestTool.getABCPath();
        Set<BITableSourceRelationPath> abcPathSet = new HashSet<BITableSourceRelationPath>();
        abcPathSet.add(abcPath);
        Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
        relations.add(BITableSourceRelationTestTool.getMemoryBC());
        Set<BICubeGenerateRelationPath> biTableRelationPath4CubeGenerateSet = calculateDependManager4Test.calRelationPath(abcPathSet, relations);
        AtomicReference<BICubeGenerateRelationPath> biTableRelationPath4CubeGenerate = new AtomicReference<BICubeGenerateRelationPath>();
        Iterator<BICubeGenerateRelationPath> iterator = biTableRelationPath4CubeGenerateSet.iterator();
        while (iterator.hasNext()) {
            biTableRelationPath4CubeGenerate.set(iterator.next());
            break;
        }
        assertTrue(biTableRelationPath4CubeGenerate.get().getDependRelationPathSet().size() == 1);
        try {
            Set<String> ids = new HashSet<String>();
            for (BITableSourceRelationPath biTableSourceRelationPath : biTableRelationPath4CubeGenerate.get().getDependRelationPathSet()) {
                ids.add(biTableSourceRelationPath.getSourceID());
            }
            BITableSourceRelationPath pathCopy = new BITableSourceRelationPath();
            pathCopy.copyFrom(biTableRelationPath4CubeGenerate.get().getBiTableSourceRelationPath());
            pathCopy.removeLastRelation();
            assertTrue(ids.contains(new BITableSourceRelationPath(abcPath.getLastRelation()).getSourceID()));
        } catch (BITablePathEmptyException e) {
            assertFalse(true);
        }
    }

    private Set<CubeTableSource> addTableC() {
        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
        cubeTableSourceSet.add(BIMemoryDataSourceFactory.generateTableC());
        return cubeTableSourceSet;
    }

    private Set<CubeTableSource> addTableABC() {
        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
        cubeTableSourceSet.add(BIMemoryDataSourceFactory.generateTableA());
        cubeTableSourceSet.add(BIMemoryDataSourceFactory.generateTableB());
        cubeTableSourceSet.add(BIMemoryDataSourceFactory.generateTableC());
        return cubeTableSourceSet;
    }

}
