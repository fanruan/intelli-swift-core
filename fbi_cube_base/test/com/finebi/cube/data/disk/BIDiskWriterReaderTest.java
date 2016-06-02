package com.finebi.cube.data.disk;

import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.disk.reader.primitive.BIByteNIOReader;
import com.finebi.cube.data.disk.writer.primitive.BIByteNIOWriter;
import com.finebi.cube.tools.BILocationBuildTestTool;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDiskWriterReaderTest extends TestCase {
    private ICubePrimitiveResourceDiscovery discovery;
    public static String projectPath = "D:\\FineBI\\Git\\workHouse\\project\\fbi_cube_base\\test\\cube\\";
//    public static String projectPath = "/Users/wuk/Documents/fbicode/";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        discovery = BICubeDiskPrimitiveDiscovery.getInstance();
    }

    private void down() {

    }

    public void testSimpleWriteReader() {
        try {
            ICubeResourceLocation location = BILocationBuildTestTool.buildWrite(projectPath, "writer");
            location.setByteType();
            BIByteNIOWriter writer = (BIByteNIOWriter) discovery.getCubeWriter(location);
            writer.recordSpecificPositionValue(0l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(1l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(2l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(3l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(4l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(5l, Byte.valueOf("35"));
            location.setReaderSourceLocation();
            BIByteNIOReader reader = (BIByteNIOReader) discovery.getCubeReader(location);
            assertEquals(reader.getSpecificValue(0l), Byte.valueOf("35"));
            assertEquals(reader.getSpecificValue(1l), Byte.valueOf("35"));
            assertEquals(reader.getSpecificValue(2l), Byte.valueOf("35"));
            assertEquals(reader.getSpecificValue(3l), Byte.valueOf("35"));
            assertEquals(reader.getSpecificValue(4l), Byte.valueOf("35"));
            assertEquals(reader.getSpecificValue(5l), Byte.valueOf("35"));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testSimpleWriteReaderOpenTime() {
        try {
            long time = System.currentTimeMillis();
            ICubeResourceLocation location = BILocationBuildTestTool.buildWrite(projectPath, "writer");
            location.setByteType();

            for (int i = 0; i < 100000; i++) {
                location.setWriterSourceLocation();

                BIByteNIOWriter writer = (BIByteNIOWriter) discovery.getCubeWriter(location);
                location.setReaderSourceLocation();

                BIByteNIOReader reader = (BIByteNIOReader) discovery.getCubeReader(location);
            }
            System.out.println(System.currentTimeMillis() - time);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
    public void testSimpleObjectOpenTime() {
        try {
            ICubeResourceLocation location = BILocationBuildTestTool.buildWrite(projectPath, "writer");

            long time = System.currentTimeMillis();

            for (int i = 0; i < 100000; i++) {
                location.setWriterSourceLocation();
                BIByteNIOWriter writer = new BIByteNIOWriter("D:\\temp\\arrayBasic.xml");
            }
            System.out.println(System.currentTimeMillis() - time);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}
