package com.finebi.cube.gen.arrange;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
import com.finebi.cube.gen.subset.BICubeBuildProbeTool;
import com.finebi.cube.impl.message.BIMessageTestTool;
import com.finebi.cube.impl.operate.BIOperationID;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.tools.BIMemoryDataSource;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.relation.BITableSourceRelationPath;

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

    public void testBasic() {
        try {
            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
            Set<ICubeTableSource> setSource = new HashSet<ICubeTableSource>();
            operationManager = new BICubeOperationManager4Test(cube, setSource);
            operationManager.initialWatcher();
            manager.registerDataSource(BIMemoryDataSourceFactory.getDataSourceSet());
//            manager.registerDataSource(setSource);
            Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
            relations.add(BITableSourceRelationTestTool.getMemoryAB());
            relations.add(BITableSourceRelationTestTool.getMemoryBC());
            manager.registerRelation(relations);
            Set<BITableSourceRelationPath> pathSet = new HashSet<BITableSourceRelationPath>();
            pathSet.add(BITableSourceRelationPathTestTool.getABCPath());
            manager.registerTableRelationPath(pathSet);
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

    public void testTable() {
        try {
            BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
            Set<ICubeTableSource> setSource = new HashSet<ICubeTableSource>();
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
            Set<ICubeTableSource> setSource = new HashSet<ICubeTableSource>();
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
        BICubeFieldSource[] fields = biMemoryDataSource.getFieldsArray(null);
        for (int i = 0; i < fields.length; i++) {
            assertTrue(map.containsKey(fields[i].getFieldName() + biMemoryDataSource.getSourceID()));
        }
    }
}
