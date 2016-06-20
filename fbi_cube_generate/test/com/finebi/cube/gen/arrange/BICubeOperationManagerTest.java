package com.finebi.cube.gen.arrange;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
import com.finebi.cube.gen.subset.BICubeBuildProbeTool;
import com.finebi.cube.impl.message.BIMessageTestTool;
import com.finebi.cube.impl.operate.BIOperationID;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.tools.*;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeOperationManagerTest extends BICubeTestBase {
    private BICubeOperationManager4Test operationManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    public BICubeOperationManagerTest() throws Exception {
        super.setUp();
    }
    public void testBasic() {
        try {
            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
            Set<CubeTableSource> setSource = new HashSet<CubeTableSource>();
            operationManager = new BICubeOperationManager4Test(cube, setSource);
            operationManager.initialWatcher();
            manager.registerDataSource(BIMemoryDataSourceFactory.getDataSourceSet());
            manager.registerRelation(BITableSourceRelationTestTool.getRelationSetABC());
            Set<BITableSourceRelationPath> pathSet = new HashSet<BITableSourceRelationPath>();
            pathSet.add(BITableSourceRelationPathTestTool.getABCPath());
            manager.registerTableRelationPath(pathSet);
            operationManager.generateRelationBuilder(BIMemoryCubeGenerateFactory.getCubeGenerateRelationABC());
            operationManager.generateTableRelationPath(BIMemoryCubeGenerateFactory.getCubeGenerateRelationPathABC());
            operationManager.generateDataSource(BIMemoryDataSourceFactory.getDataSourceSetMap());
            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            router.deliverMessage(BIMessageTestTool.generateMessageDataSourceStart());
            BICubeFinishObserver<Future> observer = new BICubeFinishObserver(new BIOperationID("finishObserver"));
            Future future = observer.getOperationResult();
            System.out.println(future.get());
            Map<String, Integer> map = BICubeBuildProbeTool.INSTANCE.getFlag();
            assertTrue(map.containsKey("tablePath"));
            assertTrue(map.containsKey("RelationIndex"));
            checkTable((BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableA(), map);
            checkTable((BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableB(), map);
            checkTable((BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableC(), map);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testTable() {
        try {
            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
            Set<CubeTableSource> setSource = new HashSet<CubeTableSource>();
            operationManager = new BICubeOperationManager4Test(cube, setSource);
            operationManager.initialWatcher();
            manager.registerDataSource(BIMemoryDataSourceFactory.getDataSourceSet());
           ;
            manager.registerTableRelationPath(BITableSourceRelationPathTestTool.getRelationPathSetABC());
            operationManager.generateDataSource(BIMemoryDataSourceFactory.getDataSourceSetMap());
            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            router.deliverMessage(BIMessageTestTool.generateMessageDataSourceStart());
            BICubeFinishObserver<Future> observer = new BICubeFinishObserver(new BIOperationID("finishObserver"));
            Future future = observer.getOperationResult();
            System.out.println(future.get());
            Map<String, Integer> map = BICubeBuildProbeTool.INSTANCE.getFlag();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /*关联关系中，取消部分依赖
        * 假设A已经存在，现在更新relationAB*/
    public void testTableRelation() {
        try {
            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
            Set<CubeTableSource> setSource = new HashSet<CubeTableSource>();
            operationManager = new BICubeOperationManager4Test(cube, setSource);
            operationManager.initialWatcher();

            Set<CubeTableSource> dataSourceSet = BIMemoryDataSourceFactory.getDataSourceSetWithAB();
            manager.registerDataSource(dataSourceSet);
            Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
            relations.add(BITableSourceRelationTestTool.getMemoryAB());
            manager.registerRelation(relations);
            operationManager.generateDataSource(BIMemoryDataSourceFactory.getDataSourceSetMapWithB());
            Set<CubeTableSource> analysisTableSources=new HashSet<CubeTableSource>();
            analysisTableSources.add(BIMemoryDataSourceFactory.generateTableB());
            BICubeGenerateRelation biCubeGenerateRelation = new BICubeGenerateRelation(BITableSourceRelationTestTool.getMemoryAB(),analysisTableSources);
            Set<BICubeGenerateRelation> biTableSourceRelationSet=new HashSet<BICubeGenerateRelation>();
            biTableSourceRelationSet.add(biCubeGenerateRelation);
            operationManager.generateRelationBuilder(biTableSourceRelationSet);
            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            router.deliverMessage(BIMessageTestTool.generateMessageDataSourceStart());
            BICubeFinishObserver<Future> observer = new BICubeFinishObserver(new BIOperationID("finishObserver"));
            Future future = observer.getOperationResult();
            System.out.println(future.get());
            Map<String, Integer> map = BICubeBuildProbeTool.INSTANCE.getFlag();
            assertFalse(map.containsKey(BIMemoryDataSourceFactory.generateTableA().getSourceID()));
            assertTrue(map.containsKey(BIMemoryDataSourceFactory.generateTableB().getSourceID()));
            assertTrue(map.containsKey("RelationIndex"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testDependTable() {
        try {
            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
            Set<CubeTableSource> setSource = new HashSet<CubeTableSource>();
            operationManager = new BICubeOperationManager4Test(cube, setSource);
            operationManager.initialWatcher();

            manager.registerDataSource(BIMemoryDataSourceFactory.getDataSourceSet());
            Set<BITableSourceRelationPath> pathSet = new HashSet<BITableSourceRelationPath>();
            pathSet.add(BITableSourceRelationPathTestTool.getABCPath());
            manager.registerTableRelationPath(pathSet);
            operationManager.generateDataSource(BIMemoryDataSourceFactory.getDataSourceSetMap_Line());
            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            router.deliverMessage(BIMessageTestTool.generateMessageDataSourceStart());
            BICubeFinishObserver<Future> observer = new BICubeFinishObserver(new BIOperationID("finishObserver"));
            Future future = observer.getOperationResult();
            System.out.println(future.get());
            Map<String, Integer> map = BICubeBuildProbeTool.INSTANCE.getFlag();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private void checkTable(BIMemoryDataSource biMemoryDataSource, Map<String, Integer> map) {
        ICubeFieldSource[] fields = biMemoryDataSource.getFieldsArray(null);
        for (int i = 0; i < fields.length; i++) {
            assertTrue(map.containsKey(fields[i].getFieldName() + biMemoryDataSource.getSourceID()));
        }
    }
}
