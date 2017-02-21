package com.finebi.cube.structure.detail;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.location.BICubeConfigurationTest;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.tools.BIUrlCutTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.file.BIFileUtils;
import junit.framework.TestCase;

import java.io.File;
import java.net.URI;

/**
 * This class created on 2016/5/2.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleDetailDataTest extends TestCase {
    private BICubeDoubleDetailData detailData;
    private ICubeResourceRetrievalService retrievalService;
    private ICubeConfiguration cubeConfiguration;
    private ICubeResourceLocation location;


    public BICubeDoubleDetailDataTest() {
        try {
            cubeConfiguration = new BICubeConfigurationTest();
            retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
            location = retrievalService.retrieveResource(new BITableKey(BITableSourceTestTool.getDBTableSourceD()));
            location.setBaseLocation(new URI(BIUrlCutTestTool.joinUrl(BIUrlCutTestTool.cutUrl("testFolder",location.getAbsolutePath()),"testFolder","//double")));
            detailData = new BICubeDoubleDetailData(BIFactoryHelper.getObject(ICubeResourceDiscovery.class),location);
        } catch (BICubeResourceAbsentException e) {
            assertFalse(true);
        } catch (Exception e1) {
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
            assertFalse(detailData.isCubeReaderAvailable());
            assertFalse(detailData.isCubeWriterAvailable());
            detailData.addDetailDataValue(0, Double.valueOf("1"));
            assertFalse(detailData.isCubeReaderAvailable());
            assertTrue(detailData.isCubeWriterAvailable());
            detailData.forceReleaseWriter();
            assertEquals(Double.valueOf("1"), detailData.getOriginalValueByRow(0));
            assertTrue(detailData.isCubeReaderAvailable());
            assertFalse(detailData.isCubeWriterAvailable());
            detailData.forceReleaseReader();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testReset() {
        try {
            assertFalse(detailData.isCubeReaderAvailable());
            assertFalse(detailData.isCubeWriterAvailable());
            detailData.addDetailDataValue(0, Double.valueOf("1"));
            assertTrue(detailData.isCubeWriterAvailable());
            detailData.resetCubeWriter();
            assertFalse(detailData.isCubeWriterAvailable());

            assertEquals(Double.valueOf("1"), detailData.getOriginalValueByRow(0));
            assertTrue(detailData.isCubeReaderAvailable());
            detailData.resetCubeReader();
            assertFalse(detailData.isCubeReaderAvailable());
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testResetInitial() {
        try {
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }
}
