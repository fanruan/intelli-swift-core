//package com.finebi.cube.gen.arrange;
//
//import com.finebi.cube.BICubeTestBase;
//import com.finebi.cube.conf.CalculateDepend;
//import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
//import com.finebi.cube.gen.subset.BICubeBuildProbeTool;
//import com.finebi.cube.impl.conf.CalculateDependManager4Test;
//import com.finebi.cube.impl.message.BIMessageTestTool;
//import com.finebi.cube.impl.operate.BIOperationID;
//import com.finebi.cube.relation.BITableSourceRelation;
//import com.finebi.cube.relation.BICubeGenerateRelation;
//import com.finebi.cube.relation.BITableSourceRelationPath;
//import com.finebi.cube.relation.BICubeGenerateRelationPath;
//import com.finebi.cube.router.IRouter;
//import com.finebi.cube.tools.BIMemoryDataSourceFactory;
//import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
//import com.finebi.cube.tools.BITableSourceRelationTestTool;
//import com.fr.bi.common.factory.BIFactoryHelper;
//import com.fr.bi.stable.data.source.CubeTableSource;
//
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.Future;
//
///**
// * Created by kary on 2016/6/13.
// * 解除部分依赖
// */
//public class BICubeOperationManager4IncrementalTest extends BICubeTestBase {
//    private BICubeOperationManager4Test operationManager;
//    private CalculateDepend calculateDependManager4Test;
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        init();
//    }
//
//    private void init() {
//        calculateDependManager4Test = new CalculateDependManager4Test();
//        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
//        cubeTableSourceSet.add(BIMemoryDataSourceFactory.generateTableC());
//        calculateDependManager4Test.setOriginal(cubeTableSourceSet);
//    }
//
//
//    /*path中解依赖
//    假设AB表以及AB关联已经生成，现在添加C表并更新ABC的PATH*/
//    public void testDependPath() {
//        try {
//            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
//            Set<CubeTableSource> setSource = new HashSet<CubeTableSource>();
//            operationManager = new BICubeOperationManager4Test(cube, setSource);
//            operationManager.initialWatcher();
//
//            Set<CubeTableSource> dataSourceSet = new HashSet<CubeTableSource>();
//            dataSourceSet.add(BIMemoryDataSourceFactory.generateTableB());
//            dataSourceSet.add(BIMemoryDataSourceFactory.generateTableC());
//            manager.registerDataSource(dataSourceSet);
//            Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
//            relations.add(BITableSourceRelationTestTool.getMemoryBC());
//            manager.registerRelation(relations);
//            Set<BITableSourceRelationPath> pathSet = new HashSet<BITableSourceRelationPath>();
//            pathSet.add(BITableSourceRelationPathTestTool.getABCPath());
//            manager.registerFieldRelationPath(pathSet);
//            operationManager.generateRelationBuilder(relations);
//            Set<BITableSourceRelationPath> biTableSourceRelationPath4IncremetalSet = new HashSet<BITableSourceRelationPath>();
//            BITableSourceRelationPath abcPath = BITableSourceRelationPathTestTool.getABCPath();
//            Set<BITableSourceRelation> relationsGenerated = new HashSet<BITableSourceRelation>();
//            relationsGenerated.add(BITableSourceRelationTestTool.getMemoryBC());
//            BICubeGenerateRelationPath biTableSourceRelationPath4Incremetal = calculateDependManager4Test.calRelationPath(abcPath, relationsGenerated);
////            biTableSourceRelationPath4IncremetalSet.add(biTableSourceRelationPath4Incremetal);
////            operationManager.generatePath4Incremental(biTableSourceRelationPath4IncremetalSet);
//            operationManager.generateTableRelationPath(biTableSourceRelationPath4IncremetalSet);
//            operationManager.generateDataSource(BIMemoryDataSourceFactory.getDataSourceSetMapWithBC());
//            IRouter router = BIFactoryHelper.getObject(IRouter.class);
//            router.deliverMessage(BIMessageTestTool.generateMessageDataSourceStart());
//            BICubeFinishObserver<Future> observer = new BICubeFinishObserver(new BIOperationID("finishObserver"));
//            Future future = observer.getOperationResult();
//            System.out.println(future.get());
//            Map<String, Integer> map = BICubeBuildProbeTool.INSTANCE.getFlag();
//            /*原理需要更新AB,BC两个relaiont和一个path，现在只要更新BC和整个path*/
//            assertTrue(map.containsKey("tablePath"));
//            //该path所依赖的relation的sourceID
//            String sourceID = new BITableSourceRelationPath(BITableSourceRelationTestTool.getMemoryBC()).getSourceID();
//            assertTrue(map.containsKey("tablePath:" + sourceID));
//            assertTrue(map.containsKey("RelationIndex:" + BIMemoryDataSourceFactory.generateTableB().getSourceID()));
//            assertFalse(map.containsKey("RelationIndex" + BIMemoryDataSourceFactory.generateTableA().getSourceID()));
//        } catch (Exception e) {
//            e.printStackTrace();
//            assertTrue(false);
//        }
//    }
//
//    /*关联关系中，取消部分依赖
//    * 假设A已经存在，现在更新relationAB*/
//    public void testTableRelation() {
//        try {
//            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
//            Set<CubeTableSource> setSource = new HashSet<CubeTableSource>();
//            operationManager = new BICubeOperationManager4Test(cube, setSource);
//            operationManager.initialWatcher();
//
//            Set<CubeTableSource> dataSourceSet = new HashSet<CubeTableSource>();
//            dataSourceSet.add(BIMemoryDataSourceFactory.generateTableA());
//            dataSourceSet.add(BIMemoryDataSourceFactory.generateTableB());
//            manager.registerDataSource(dataSourceSet);
//            Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
//            relations.add(BITableSourceRelationTestTool.getMemoryAB());
//            manager.registerRelation(relations);
//            operationManager.generateDataSource(BIMemoryDataSourceFactory.getDataSourceSetMapWithB());
//            Set<CubeTableSource> analysisTableSources=new HashSet<CubeTableSource>();
//            analysisTableSources.add(BIMemoryDataSourceFactory.generateTableB());
//            BICubeGenerateRelation biTableSourceRelation4Incremental = new BICubeGenerateRelation(BITableSourceRelationTestTool.getMemoryAB(),analysisTableSources);
//            Set<BITableSourceRelation> bITableSourceRelation4Incremental = new HashSet<BITableSourceRelation>();
////            bITableSourceRelation4Incremental.add(biTableSourceRelation4Incremental);
////            operationManager.generateRelation4Incremental(bITableSourceRelation4Incremental);
//            operationManager.generateRelationBuilder(bITableSourceRelation4Incremental);
//            IRouter router = BIFactoryHelper.getObject(IRouter.class);
//            router.deliverMessage(BIMessageTestTool.generateMessageDataSourceStart());
//            BICubeFinishObserver<Future> observer = new BICubeFinishObserver(new BIOperationID("finishObserver"));
//            Future future = observer.getOperationResult();
//            System.out.println(future.get());
//            Map<String, Integer> map = BICubeBuildProbeTool.INSTANCE.getFlag();
//            assertFalse(map.containsKey(BIMemoryDataSourceFactory.generateTableA().getSourceID()));
//            assertTrue(map.containsKey(BIMemoryDataSourceFactory.generateTableB().getSourceID()));
//            assertTrue(map.containsKey("RelationIndex"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            assertTrue(false);
//        }
//    }
//
//    /*测试stop*/
//    public void testStopStatus() {
//
//    }
//
//}
