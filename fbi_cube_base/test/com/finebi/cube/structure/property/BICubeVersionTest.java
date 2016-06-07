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
 * This class created on 2016/5/2.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeVersionTest extends TestCase {
    private BICubeVersion version;
    private ICubeResourceRetrievalService retrievalService;
    private ICubeConfiguration cubeConfiguration;
    private ICubeResourceLocation location;


    public BICubeVersionTest() {
        try {
            cubeConfiguration = new BICubeConfigurationTest();
            retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
            location = retrievalService.retrieveResource(new BITableKey(BITableSourceTestTool.getDBTableSourceD()));
            version = new BICubeVersion(location, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
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
            assertFalse(version.isReaderAvailable());
            assertFalse(version.isWriterAvailable());
            version.addVersion(1);
            assertTrue(version.isWriterAvailable());
            assertFalse(version.isReaderAvailable());
            assertEquals(version.getCubeVersion(), 1);
            assertTrue(version.isWriterAvailable());
            assertTrue(version.isReaderAvailable());
        } catch (Exception e) {
            assertFalse(true);
        }
    }

    public void testReset() {
        try {
            testAvailable();
            version.resetWriter();
            assertFalse(version.isWriterAvailable());
            assertTrue(version.isReaderAvailable());
            version.resetReader();

            assertFalse(version.isWriterAvailable());
            assertFalse(version.isReaderAvailable());

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
