package com.finebi.cube.data.disk;

import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.disk.reader.primitive.BIByteNIOReader;
import com.finebi.cube.data.disk.writer.primitive.BIByteNIOWriter;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.tools.BILocationBuildTestTool;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * This class created on 2016/6/3.
 *
 * @author Connery
 * @since 4.0
 */
public class BIPrimitiveDataCacheTest extends TestCase {
    private ICubePrimitiveResourceDiscovery discovery;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        discovery = BICubeDiskPrimitiveDiscovery.getInstance();
    }

    public void testWriteReaderRelease() {
        try {
            ICubeResourceLocation location = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer");
            location.setByteType();
            BIByteNIOWriter writer = (BIByteNIOWriter) discovery.getCubeWriter(location);
            writer.recordSpecificPositionValue(0l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(1l, Byte.valueOf("35"));

            location.setReaderSourceLocation();
            BIByteNIOReader reader = (BIByteNIOReader) discovery.getCubeReader(location);
            assertEquals(reader.getSpecificValue(0l), Byte.valueOf("35"));
            assertEquals(reader.getSpecificValue(1l), Byte.valueOf("35"));

            location.setWriterSourceLocation();
            BIByteNIOWriter writer_copy = (BIByteNIOWriter) discovery.getCubeWriter(location);
            location.setReaderSourceLocation();

            BIByteNIOReader reader_copy = (BIByteNIOReader) discovery.getCubeReader(location);
            assertEquals(writer.getWriterHandler(), writer_copy.getWriterHandler());
            assertEquals(reader.getReaderHandler(), reader_copy.getReaderHandler());
            reader.releaseHandler();
            assertTrue(reader_copy.canReader());
            reader.forceRelease();
            assertFalse(reader_copy.canReader());
            writer.releaseHandler();
            assertTrue(writer_copy.canWriter());
            writer.forceRelease();
            assertFalse(writer_copy.canWriter());

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }


    public void testCacheRelease() {
        try {
            ICubeResourceLocation location = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer");
            location.setByteType();
            BIByteNIOWriter writer = (BIByteNIOWriter) discovery.getCubeWriter(location);
            writer.recordSpecificPositionValue(0l, Byte.valueOf("35"));

            location.setReaderSourceLocation();
            BIByteNIOReader reader = (BIByteNIOReader) discovery.getCubeReader(location);
            assertEquals(reader.getSpecificValue(0l), Byte.valueOf("35"));
            assertTrue(writer.canWriter());
            assertTrue(reader.canReader());
            BICubeDiskPrimitiveDiscovery.getInstance().forceRelease();
            assertFalse(writer.canWriter());
            assertFalse(reader.canReader());

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}
