package com.finebi.cube.gen.arrange;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.gen.mes.BIStatusUtils;
import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
import com.finebi.cube.gen.subset.BICubeBuildProbeTool;
import com.finebi.cube.impl.message.BIMessageTestTool;
import com.finebi.cube.impl.operate.BIOperationID;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.tools.BIMemoryDataSource;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.*;
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
            Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
            relations.add(BITableSourceRelationTestTool.getMemoryAB());
            relations.add(BITableSourceRelationTestTool.getMemoryBC());
            manager.registerRelation(relations);
            Set<BITableSourceRelationPath> pathSet = new HashSet<BITableSourceRelationPath>();
            pathSet.add(BITableSourceRelationPathTestTool.getABCPath());
            operationManager.generateRelationBuilder(relations);
            operationManager.generateTableRelationPath(pathSet);
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

    public void testDependPath() {
        try {
//            testBasic();
            /*添加表D及相关的relation*/
            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
            Set<CubeTableSource> setSource = new HashSet<CubeTableSource>();
            operationManager = new BICubeOperationManager4Test(cube, setSource);
            operationManager.initialWatcher();
            Set<CubeTableSource> dataSourceSet = new HashSet<CubeTableSource>();
            dataSourceSet.add(BIMemoryDataSourceFactory.generateTableC());
            dataSourceSet.add(BIMemoryDataSourceFactory.generateTableB());
            manager.registerDataSource(dataSourceSet);
            Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
            relations.add(BITableSourceRelationTestTool.getMemoryBC());
            manager.registerRelation(relations);
            Set<BITableSourceRelationPath> pathSet = new HashSet<BITableSourceRelationPath>();
            pathSet.add(BITableSourceRelationPathTestTool.getABCPath());
            /*解开path依赖*/
            Map<BITableSourceRelationPath, Set<IStatusTag>> setSetMap = new HashMap<BITableSourceRelationPath, Set<IStatusTag>>();
            if (pathSet != null && !pathSet.isEmpty()) {
                for (BITableSourceRelationPath biTableSourceRelationPath : pathSet) {
                    Set<IStatusTag> dependsStatusTag = new HashSet<IStatusTag>();
                    BITableSourceRelationPath frontPath = new BITableSourceRelationPath();
                    frontPath.copyFrom(biTableSourceRelationPath);
                    frontPath.removeLastRelation();
                    dependsStatusTag.add(BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.PATH_TOPIC, new BITableSourceRelationPath(biTableSourceRelationPath.getLastRelation()).getSourceID()));
                    setSetMap.put(biTableSourceRelationPath, dependsStatusTag);
                }
            }
            operationManager.generateRelationBuilder(relations);
            operationManager.generateTableRelationPath(setSetMap);
            operationManager.generateDataSource(BIMemoryDataSourceFactory.getDataSourceSetMap());

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

    public void testTable() {
        try {
            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
            Set<CubeTableSource> setSource = new HashSet<CubeTableSource>();
            operationManager = new BICubeOperationManager4Test(cube, setSource);
            operationManager.initialWatcher();

            manager.registerDataSource(BIMemoryDataSourceFactory.getDataSourceSet());
            Set<BITableSourceRelationPath> pathSet = new HashSet<BITableSourceRelationPath>();
            pathSet.add(BITableSourceRelationPathTestTool.getABCPath());
            manager.registerTableRelationPath(pathSet);
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
