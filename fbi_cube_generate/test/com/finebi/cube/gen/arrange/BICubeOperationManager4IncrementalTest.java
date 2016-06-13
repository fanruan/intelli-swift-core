package com.finebi.cube.gen.arrange;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
import com.finebi.cube.gen.subset.BICubeBuildProbeTool;
import com.finebi.cube.impl.message.BIMessageTestTool;
import com.finebi.cube.impl.operate.BIOperationID;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelation4Incremental;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.relation.BITableSourceRelationPath4Incremetal;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Created by kary on 2016/6/13.
 * 解除部分依赖
 */
public class BICubeOperationManager4IncrementalTest extends BICubeTestBase {
    private BICubeOperationManager4Test operationManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    public BICubeOperationManager4IncrementalTest() throws Exception {
        super.setUp();
    }

    /*假设AB表以及AB关联已经生成，现在添加C表并更新ABC的PATH*/
    public void testDependPath() {
        try {
            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
            Set<CubeTableSource> setSource = new HashSet<CubeTableSource>();
            operationManager = new BICubeOperationManager4Test(cube, setSource);
            operationManager.initialWatcher();

            Set<CubeTableSource> dataSourceSet = new HashSet<CubeTableSource>();
            dataSourceSet.add(BIMemoryDataSourceFactory.generateTableB());
            dataSourceSet.add(BIMemoryDataSourceFactory.generateTableC());
            manager.registerDataSource(dataSourceSet);
            Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
            relations.add(BITableSourceRelationTestTool.getMemoryBC());
            manager.registerRelation(relations);
            Set<BITableSourceRelationPath> pathSet = new HashSet<BITableSourceRelationPath>();
            pathSet.add(BITableSourceRelationPathTestTool.getABCPath());
            manager.registerFieldRelationPath(pathSet);
            operationManager.generateRelationBuilder(relations);
            Set<BITableSourceRelationPath4Incremetal> biTableSourceRelationPath4IncremetalSet = new HashSet<BITableSourceRelationPath4Incremetal>();
            Set<BITableSourceRelation> relationsTemp = new HashSet<BITableSourceRelation>();
            relationsTemp.add(BITableSourceRelationTestTool.getMemoryBC());
            BITableSourceRelationPath4Incremetal biTableSourceRelationPath4Incremetal = new BITableSourceRelationPath4Incremetal(BITableSourceRelationPathTestTool.getABCPath(), relationsTemp);
            biTableSourceRelationPath4IncremetalSet.add(biTableSourceRelationPath4Incremetal);
            operationManager.generateTableRelationPath4Incremental(biTableSourceRelationPath4IncremetalSet);
            operationManager.generateDataSource(BIMemoryDataSourceFactory.getDataSourceSetMapWithBC());
            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            router.deliverMessage(BIMessageTestTool.generateMessageDataSourceStart());
            BICubeFinishObserver<Future> observer = new BICubeFinishObserver(new BIOperationID("finishObserver"));
            Future future = observer.getOperationResult();
            System.out.println(future.get());
            Map<String, Integer> map = BICubeBuildProbeTool.INSTANCE.getFlag();
            assertTrue(map.containsKey("tablePath"));
            assertTrue(map.containsKey("RelationIndex"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /*关联关系中，取消部分依赖*/
    public void testTableRelation() {
        try {
            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
            Set<CubeTableSource> setSource = new HashSet<CubeTableSource>();
            operationManager = new BICubeOperationManager4Test(cube, setSource);
            operationManager.initialWatcher();

            Set<CubeTableSource> dataSourceSet = new HashSet<CubeTableSource>();
            dataSourceSet.add(BIMemoryDataSourceFactory.generateTableA());
            dataSourceSet.add(BIMemoryDataSourceFactory.generateTableB());
            manager.registerDataSource(dataSourceSet);
            Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
            relations.add(BITableSourceRelationTestTool.getMemoryAB());
            manager.registerRelation(relations);
            operationManager.generateDataSource(BIMemoryDataSourceFactory.getDataSourceSetMapWithAB());
            BITableSourceRelation4Incremental biTableSourceRelation4Incremental = new BITableSourceRelation4Incremental(BITableSourceRelationTestTool.getMemoryAB(), BIMemoryDataSourceFactory.generateTableA());
            Set<BITableSourceRelation4Incremental> bITableSourceRelation4Incremental = new HashSet<BITableSourceRelation4Incremental>();
            bITableSourceRelation4Incremental.add(biTableSourceRelation4Incremental);
            operationManager.generateRelationBuilder4Incremental(bITableSourceRelation4Incremental);
            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            router.deliverMessage(BIMessageTestTool.generateMessageDataSourceStart());
            BICubeFinishObserver<Future> observer = new BICubeFinishObserver(new BIOperationID("finishObserver"));
            Future future = observer.getOperationResult();
            System.out.println(future.get());
            Map<String, Integer> map = BICubeBuildProbeTool.INSTANCE.getFlag();
            assertTrue(map.containsKey("RelationIndex"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testStopStatus() {

    }

}
