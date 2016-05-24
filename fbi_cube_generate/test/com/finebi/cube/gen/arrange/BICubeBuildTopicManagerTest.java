package com.finebi.cube.gen.arrange;

import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.gen.mes.BIFragmentUtils;
import com.finebi.cube.gen.mes.BITopicUtils;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.finebi.cube.relation.BITableSourceRelation;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeBuildTopicManagerTest extends TestCase {

    public void testRegisterTable() {
        BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
        manager.registerDataSource(BIMemoryDataSourceFactory.getDataSourceSet());
        IRouter router = BIFactoryHelper.getObject(IRouter.class);

        assertTrue(router.isRegistered(BICubeBuildTopicTag.PATH_TOPIC));
        assertTrue(router.isRegistered(BICubeBuildTopicTag.DATA_SOURCE_TOPIC));
        assertTrue(router.isRegistered(BICubeBuildTopicTag.FINISH_BUILD_CUBE));
        assertTrue(router.isRegistered(BICubeBuildTopicTag.START_BUILD_CUBE));
        assertFalse(router.isRegistered(BICubeBuildTopicTag.STOP_BUILD_CUBE));

        assertTrue(router.isRegistered(BITopicUtils.generateTopicTag(BIMemoryDataSourceFactory.generateTableA())));
        assertTrue(router.isRegistered(BITopicUtils.generateTopicTag(BIMemoryDataSourceFactory.generateTableB())));
        assertTrue(router.isRegistered(BITopicUtils.generateTopicTag(BIMemoryDataSourceFactory.generateTableC())));

        assertTrue(router.isRegistered(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.DATA_SOURCE_TOPIC, BIMemoryDataSourceFactory.generateTableA())));
        assertTrue(router.isRegistered(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.DATA_SOURCE_TOPIC, BIMemoryDataSourceFactory.generateTableB())));
        assertTrue(router.isRegistered(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.DATA_SOURCE_TOPIC, BIMemoryDataSourceFactory.generateTableC())));
        for (int i = 0; i < BIMemoryDataSourceFactory.generateTableA().getFieldsArray(null).length; i++) {
            BICubeFieldSource field = BIMemoryDataSourceFactory.generateTableA().getFieldsArray(null)[i];
            checkRegisterMethod(router, field);

        }
        for (int i = 0; i < BIMemoryDataSourceFactory.generateTableB().getFieldsArray(null).length; i++) {
            BICubeFieldSource field = BIMemoryDataSourceFactory.generateTableB().getFieldsArray(null)[i];
            checkRegisterMethod(router, field);

        }
        for (int i = 0; i < BIMemoryDataSourceFactory.generateTableC().getFieldsArray(null).length; i++) {
            BICubeFieldSource field = BIMemoryDataSourceFactory.generateTableC().getFieldsArray(null)[i];
            checkRegisterMethod(router, field);

        }

    }

    private void checkRegisterMethod(IRouter router, BICubeFieldSource field) {
        Iterator<BIColumnKey> it = BIColumnKey.generateColumnKey(field).iterator();
        while (it.hasNext()) {
            BIColumnKey columnKey = it.next();
            router.isRegistered(BIFragmentUtils.generateFragment(BITopicUtils.generateTopicTag(BIMemoryDataSourceFactory.generateTableB())
                    , columnKey.getFullName()));
        }
    }

    public void testRegisterRelation() {
        BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
        IRouter router = BIFactoryHelper.getObject(IRouter.class);
        Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
        relations.add(BITableSourceRelationTestTool.getAB());
        relations.add(BITableSourceRelationTestTool.getAC());
        manager.registerRelation(relations);
        assertTrue(router.isRegistered(BICubeBuildTopicTag.PATH_TOPIC));
        assertTrue(router.isRegistered(BICubeBuildTopicTag.DATA_SOURCE_TOPIC));
        assertTrue(router.isRegistered(BICubeBuildTopicTag.FINISH_BUILD_CUBE));
        assertTrue(router.isRegistered(BICubeBuildTopicTag.START_BUILD_CUBE));
        assertFalse(router.isRegistered(BICubeBuildTopicTag.STOP_BUILD_CUBE));

        assertTrue(router.isRegistered(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.PATH_TOPIC, BITableSourceRelationTestTool.getAB())));
        assertTrue(router.isRegistered(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.PATH_TOPIC, BITableSourceRelationTestTool.getAC())));
        /**
         * 字段关联中的字段对关联是有影响的ID。
         */
        assertFalse(router.isRegistered(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.PATH_TOPIC, BITableSourceRelationTestTool.getAaB())));
        assertFalse(router.isRegistered(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.DATA_SOURCE_TOPIC, BITableSourceRelationTestTool.getAaB())));

    }
}
