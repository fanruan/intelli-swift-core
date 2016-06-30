package com.finebi.cube.structure.property;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.location.BICubeConfigurationTest;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.utils.file.BIFileUtils;
import junit.framework.TestCase;

import java.io.File;

/**
 * Created by 小灰灰 on 2016/6/28.
 */
public class BICubeColumnPositionOfGroupServiceTest extends TestCase {
    private BICubeColumnPositionOfGroupService cubeColumnPositionOfGroupService;
    private ICubeResourceRetrievalService retrievalService;
    private ICubeConfiguration cubeConfiguration;
    private ICubeResourceLocation location;

    public BICubeColumnPositionOfGroupServiceTest() {
        try {
            cubeConfiguration = new BICubeConfigurationTest();
            retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
            location = retrievalService.retrieveResource(new BITableKey(BITableSourceTestTool.getDBTableSourceD()));
            cubeColumnPositionOfGroupService = new BICubeColumnPositionOfGroupService(location, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        } catch (BICubeResourceAbsentException e) {
            assertFalse(true);
        }
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ICubeResourceLocation location = retrievalService.retrieveResource(new BITableKey(BITableSourceTestTool.getDBTableSourceD()));
        File file = new File(location.getAbsolutePath());
        if (file.exists()) {
            BIFileUtils.delete(file);
        }
    }

    public void testAvailable() {
        try {
            assertFalse(cubeColumnPositionOfGroupService.isReaderAvailable());
            assertFalse(cubeColumnPositionOfGroupService.isWriterAvailable());
            cubeColumnPositionOfGroupService.addPositionOfGroup(1, 1);
            assertTrue(cubeColumnPositionOfGroupService.isWriterAvailable());
            assertFalse(cubeColumnPositionOfGroupService.isReaderAvailable());
            assertEquals(cubeColumnPositionOfGroupService.getPositionOfGroup(1), new Integer(1));
            assertTrue(cubeColumnPositionOfGroupService.isWriterAvailable());
            assertTrue(cubeColumnPositionOfGroupService.isReaderAvailable());
        } catch (Exception e) {
            assertFalse(true);
        }
    }

    public void testReset() {
        try {
            testAvailable();
            cubeColumnPositionOfGroupService.resetWriter();
            assertFalse(cubeColumnPositionOfGroupService.isWriterAvailable());
            assertTrue(cubeColumnPositionOfGroupService.isReaderAvailable());
            cubeColumnPositionOfGroupService.resetReader();

            assertFalse(cubeColumnPositionOfGroupService.isWriterAvailable());
            assertFalse(cubeColumnPositionOfGroupService.isReaderAvailable());

        } catch (Exception e) {
            assertFalse(true);
        }
    }

    public void testResetInitial() {
        try {
            testReset();
            testAvailable();

        } catch (Exception e) {
            assertFalse(true);
        }
    }


}