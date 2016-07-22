package com.finebi.cube.data.disk;

import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.disk.writer.primitive.BIByteNIOWriter;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.tools.BILocationBuildTestTool;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIStringUtils;

import java.io.File;

/**
 * Created by kary on 2016/7/22.
 * 调用相同句柄对同一个文件读写时，有一定概率导致jvm崩溃
 */
public class BIThreadWriterError1 extends Thread {
    private ICubePrimitiveResourceDiscovery discovery;

    public static String projectPath = computePath();

    public void run() {
         testSimpleWriteReader();
    }
    public static void main(String[] args) {
        for (int i = 0; i < 200; i++) {
            new BIThreadWriterError1().start();
        }
    }

    private BIByteNIOWriter writer;

    public BIThreadWriterError1() {
        this.discovery = BICubeDiskPrimitiveDiscovery.getInstance();
        ICubeResourceLocation location = BILocationBuildTestTool.buildWrite(projectPath, "threadWriter");
        location.setByteType();
        try {
            writer = (BIByteNIOWriter) discovery.getCubeWriter(location);
            System.out.println(writer.getWriterHandler().toString());
        } catch (IllegalCubeResourceLocationException e) {
            e.printStackTrace();
        } catch (BIBuildWriterException e) {
            e.printStackTrace();
        }
    }

    private static String computePath() {
        String classFileName = "classes";
        String libFileName = "lib";
        File directory = new File("");
        String classRootPath = com.finebi.cube.data.disk.BIDiskWriterReaderTest.class.getResource("/").getPath();
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
//        synchronized(discovery) {
        try {

            this.writer.recordSpecificPositionValue(0l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(1l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(2l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(3l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(4l, Byte.valueOf("35"));
            writer.recordSpecificPositionValue(5l, Byte.valueOf("35"));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}
