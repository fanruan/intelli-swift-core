package com.finebi.cube.structure.group;

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
import java.net.URISyntaxException;

/**
 * This class created on 2016/5/2.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIntegerGroupTest extends TestCase {
    private BICubeIntegerGroupData groupData;
    private ICubeResourceRetrievalService retrievalService;
    private ICubeConfiguration cubeConfiguration;
    private ICubeResourceLocation location;


    @Override
    protected void setUp() throws Exception {

        try {
            cubeConfiguration = new BICubeConfigurationTool();
            retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
            location = retrievalService.retrieveResource(new BITableKey(BITableSourceTestTool.getDBTableSourceD()));
            location.setBaseLocation(new URI(BIUrlCutTestTool.joinUrl(BIUrlCutTestTool.cutUrl("testFolder",location.getAbsolutePath()),"testFolder","//integer")));            groupData = new BICubeIntegerGroupData(BIFactoryHelper.getObject(ICubeResourceDiscovery.class),location);
        } catch (BICubeResourceAbsentException e) {
            assertFalse(true);
        } catch (URISyntaxException ee) {
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
        available(1, Integer.valueOf(34));
    }

    public void available(int position, Integer value) {
        try {
            assertFalse(groupData.isGroupReaderAvailable());
            assertFalse(groupData.isGroupWriterAvailable());
            assertFalse(groupData.isLengthReaderAvailable());
            assertFalse(groupData.isLengthWriterAvailable());
            groupData.addGroupDataValue(position, value);
            assertFalse(groupData.isGroupReaderAvailable());
            assertTrue(groupData.isGroupWriterAvailable());
            assertFalse(groupData.isLengthReaderAvailable());
            assertFalse(groupData.isLengthWriterAvailable());
            groupData.forceReleaseWriter();
            assertEquals(value, groupData.getGroupObjectValueByPosition(position));
            assertTrue(groupData.isGroupReaderAvailable());
            assertFalse(groupData.isGroupWriterAvailable());
            assertFalse(groupData.isLengthReaderAvailable());
            assertFalse(groupData.isLengthWriterAvailable());
            groupData.forceReleaseReader();
            groupData.writeSizeOfGroup(10);
            assertFalse(groupData.isGroupReaderAvailable());
            assertFalse(groupData.isGroupWriterAvailable());
            assertFalse(groupData.isLengthReaderAvailable());
            assertTrue(groupData.isLengthWriterAvailable());
            groupData.forceReleaseWriter();
            assertEquals(10, groupData.sizeOfGroup());
            assertFalse(groupData.isGroupReaderAvailable());
            assertFalse(groupData.isGroupWriterAvailable());
            assertTrue(groupData.isLengthReaderAvailable());
            assertFalse(groupData.isLengthWriterAvailable());
            groupData.forceReleaseReader();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testReset() {
        reset(1, Integer.valueOf("123"));
    }

    public void reset(int position, Integer value) {
        try {
            assertFalse(groupData.isGroupReaderAvailable());
            assertFalse(groupData.isGroupWriterAvailable());
            assertFalse(groupData.isLengthReaderAvailable());
            assertFalse(groupData.isLengthWriterAvailable());
            groupData.addGroupDataValue(position, value);
            groupData.writeSizeOfGroup(10);
            assertTrue(groupData.isGroupWriterAvailable());
            assertTrue(groupData.isLengthWriterAvailable());
            groupData.resetGroupWriter();
            assertFalse(groupData.isGroupWriterAvailable());
            assertTrue(groupData.isLengthWriterAvailable());
            groupData.resetLengthWriter();
            assertFalse(groupData.isGroupWriterAvailable());
            assertFalse(groupData.isLengthWriterAvailable());

            assertEquals(value, groupData.getGroupObjectValueByPosition(position));
            assertEquals(10, groupData.sizeOfGroup());
            assertTrue(groupData.isGroupReaderAvailable());
            assertTrue(groupData.isLengthReaderAvailable());
            groupData.resetGroupReader();
            assertFalse(groupData.isGroupReaderAvailable());
            assertTrue(groupData.isLengthReaderAvailable());
            groupData.resetLengthReader();
            assertFalse(groupData.isGroupReaderAvailable());
            assertFalse(groupData.isLengthReaderAvailable());
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testResetInitial() {
        try {
            reset(1, Integer.valueOf("32"));
            available(1, Integer.valueOf("32"));
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }
}
