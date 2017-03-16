package com.finebi.cube.structure.detail;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.tools.BICubeConfigurationTool;
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
public class BICubeByteDetailDataTest extends TestCase {
    private BICubeByteDetailData detailData;
    private ICubeResourceRetrievalService retrievalService;
    private ICubeConfiguration cubeConfiguration;
    private ICubeResourceLocation location;

    @Override
    protected void setUp() throws Exception {

        try {
            cubeConfiguration = new BICubeConfigurationTool();
            retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
            location = retrievalService.retrieveResource(new BITableKey(BITableSourceTestTool.getDBTableSourceD()));
            location.setBaseLocation(new URI(BIUrlCutTestTool.joinUrl(BIUrlCutTestTool.cutUrl("testFolder",location.getAbsolutePath()),"testFolder","//byte")));
            detailData = new BICubeByteDetailData(BIFactoryHelper.getObject(ICubeResourceDiscovery.class), location);
        } catch (BICubeResourceAbsentException e) {
            assertFalse(true);
        } catch (Exception e1) {
            assertFalse(true);
        }

        super.setUp();
        ICubeResourceLocation location = retrievalService.retrieveResource(new BITableKey(BITableSourceTestTool.getDBTableSourceD()));
        File file = new File(location.getAbsolutePath());
        if (file.exists()) {
            BIFileUtils.delete(file);
        }
    }

    public void testAvailable() {
        try {
            assertFalse(detailData.isCubeWriterAvailable());
            assertFalse(detailData.isCubeReaderAvailable());
            detailData.addDetailDataValue(0, Byte.valueOf("12"));
            assertFalse(detailData.isCubeReaderAvailable());
            assertTrue(detailData.isCubeWriterAvailable());
            detailData.forceReleaseWriter();
            assertEquals(Byte.valueOf("12"), detailData.getOriginalObjectValueByRow(0));
            assertEquals(Byte.valueOf("12").byteValue(), detailData.getOriginalValueByRow(0));
            assertTrue(detailData.isCubeReaderAvailable());
            assertFalse(detailData.isCubeWriterAvailable());
            detailData.forceReleaseReader();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        } finally {
            detailData.forceReleaseReader();
            detailData.forceReleaseWriter();
        }
    }

    public void testReset() {
        try {
            assertFalse(detailData.isCubeWriterAvailable());
            assertFalse(detailData.isCubeReaderAvailable());
            detailData.addDetailDataValue(0, Byte.valueOf("12"));
            assertTrue(detailData.isCubeWriterAvailable());
            detailData.resetCubeWriter();
            assertFalse(detailData.isCubeWriterAvailable());

            assertEquals(Byte.valueOf("12"), detailData.getOriginalObjectValueByRow(0));
            assertEquals(Byte.valueOf("12").byteValue(), detailData.getOriginalValueByRow(0));
            assertTrue(detailData.isCubeReaderAvailable());
            detailData.resetCubeReader();
            assertFalse(detailData.isCubeReaderAvailable());
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        } finally {
            detailData.forceReleaseReader();
            detailData.forceReleaseWriter();
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
