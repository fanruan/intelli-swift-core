package com.finebi.cube.data.disk;

import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.disk.reader.primitive.BIByteNIOReader;
import com.finebi.cube.data.disk.writer.primitive.BIByteNIOWriter;
import com.finebi.cube.tools.BILocationBuildTestTool;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIStringUtils;
import junit.framework.TestCase;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDiskWriterReaderTest extends TestCase {
    private ICubePrimitiveResourceDiscovery discovery;

    public static String projectPath = computePath();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        discovery = BICubeDiskPrimitiveDiscovery.getInstance();
    }

    private void down() {

    }

    private static String computePath() {
        String classFileName = "classes";
        String libFileName = "lib";
        File directory = new File("");
        String classRootPath = BIDiskWriterReaderTest.class.getResource("/").getPath();
        classRootPath = classRootPath.replace("/", File.separator);
        if (classRootPath.endsWith(File.separator)) {
            classRootPath = cut(classRootPath, File.separator);
        }
        if (classRootPath.endsWith(classFileName)) {
            classRootPath = cut(classRootPath, classFileName);
        }
        if (classRootPath.endsWith(libFileName)) {
            classRootPath = cut(classRootPath, libFileName);
        }
        if (classRootPath.endsWith(File.separator)) {
            classRootPath = BIStringUtils.append(classRootPath, "testFolder", File.separator, "cube");
        }
        return classRootPath;
    }

    private static String cut(String path, String suffix) {
        return BIStringUtils.cutEndChar(path, suffix);
    }

    public void testPath() {
        System.out.println(computePath());
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
