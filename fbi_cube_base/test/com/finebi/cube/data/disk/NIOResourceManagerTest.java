package com.finebi.cube.data.disk;

import com.finebi.cube.data.disk.reader.BIStringNIOReader;
import com.finebi.cube.data.disk.writer.BIStringNIOWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.tools.BILocationBuildTestTool;
import junit.framework.TestCase;

/**
 * Created by wang on 2016/10/9.
 */
public class NIOResourceManagerTest extends TestCase {
    public void testReadAndRead(){
        final ICubeResourceLocation locationR = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer" + 0 + "ok");
        locationR.setStringType();
        locationR.setReaderSourceLocation();
        BICubeDiskDiscovery discovery = BICubeDiskDiscovery.getInstance();
        BIStringNIOReader reader1 = null;
        BIStringNIOReader reader2 = null;
        String r = "";
        try {
            reader1 = (BIStringNIOReader) discovery.getCubeReader(locationR);
            r = reader1.getSpecificValue(0);
//            reader1.forceRelease();
            reader2 = (BIStringNIOReader) discovery.getCubeReader(locationR);
            r = reader2.getSpecificValue(0);
            reader1.forceRelease();
            reader2.forceRelease();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
    public void testReadAndWrite(){
        final ICubeResourceLocation locationR = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer" + 0 + "ok");
        locationR.setStringType();
        locationR.setReaderSourceLocation();
        final ICubeResourceLocation locationW = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer" + 0 + "ok");
        locationW.setStringType();
        locationW.setWriterSourceLocation();
        BICubeDiskDiscovery discovery = BICubeDiskDiscovery.getInstance();
        BIStringNIOReader reader1 = null;
        BIStringNIOWriter writer1 = null;
        String r = "";
        try {
            reader1 = (BIStringNIOReader) discovery.getCubeReader(locationR);
            r = reader1.getSpecificValue(0);
//            reader1.forceRelease();

            writer1 = (BIStringNIOWriter) discovery.getCubeWriter(locationW);
            writer1.recordSpecificValue(1,"b");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
    public void testWriteAndRead(){
        final ICubeResourceLocation locationR = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer" + 0 + "ok");
        locationR.setStringType();
        locationR.setReaderSourceLocation();
        final ICubeResourceLocation locationW = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer" + 0 + "ok");
        locationW.setStringType();
        locationW.setWriterSourceLocation();
        BICubeDiskDiscovery discovery = BICubeDiskDiscovery.getInstance();
        BIStringNIOReader reader1 = null;
        BIStringNIOWriter writer1 = null;
        String r = "";
        try {
            writer1 = (BIStringNIOWriter) discovery.getCubeWriter(locationW);
            writer1.recordSpecificValue(1,"b");
//            writer1.forceRelease();
            reader1 = (BIStringNIOReader) discovery.getCubeReader(locationR);
            r = reader1.getSpecificValue(0);
//            reader1.forceRelease();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
    public void testWriteAndWrite(){
        final ICubeResourceLocation locationW = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer" + 0 + "ok");
        locationW.setStringType();
        locationW.setReaderSourceLocation();
        final ICubeResourceLocation locationW1 = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer" + 0 + "ok");
        locationW.setStringType();
        locationW.setWriterSourceLocation();
        BICubeDiskDiscovery discovery = BICubeDiskDiscovery.getInstance();
        BIStringNIOWriter writer2 = null;
        BIStringNIOWriter writer1 = null;
        String r = "";
        try {
            writer1 = (BIStringNIOWriter) discovery.getCubeWriter(locationW);
            writer1.recordSpecificValue(1,"b");
            writer1.forceRelease();
            writer2 = (BIStringNIOWriter) discovery.getCubeWriter(locationW);
            writer2.recordSpecificValue(1,"b");
//            writer2.forceRelease();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
    public void testMultiThreadRead() {
        final ICubeResourceLocation locationR = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer" + 0 + "ok");
        locationR.setStringType();
        locationR.setReaderSourceLocation();
        final ICubeResourceLocation locationW = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer" + 0 + "ok");
        locationW.setStringType();
        locationW.setWriterSourceLocation();
        final BICubeDiskDiscovery discovery = BICubeDiskDiscovery.getInstance();
        Runnable readTask = new Runnable() {
            @Override
            public void run() {
                BIStringNIOReader reader = null;
                String r = "";
                try {
                    reader = (BIStringNIOReader) discovery.getCubeReader(locationR);
                    for (int i = 0; i < 100000; i++) {
                        r = reader.getSpecificValue(0);
                        if (i % 20000 == 0) {
                            System.out.println(" r: " + r);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    reader.clear();
                    reader.forceRelease();
                }
            }
        };
        Runnable clearReadTask = new Runnable() {
            @Override
            public void run() {
                BIStringNIOReader reader = null;
                String r = "";
                try {
                    reader = (BIStringNIOReader) discovery.getCubeReader(locationR);
                    Thread.sleep((Thread.currentThread().getId() % 5 * 200));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    reader.clear();
//                    reader.forceRelease();
                }
            }
        };
        Runnable forceReleaseReadTask = new Runnable() {
            @Override
            public void run() {
                BIStringNIOReader reader = null;
                String r = "";
                try {
                    reader = (BIStringNIOReader) discovery.getCubeReader(locationR);
                    Thread.sleep((Thread.currentThread().getId() % 5 * 100));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    reader.forceRelease();
                }
            }
        };
        Runnable writeTask = new Runnable() {
            @Override
            public void run() {
                BIStringNIOWriter writer = null;
                String r = "";
                try {
                    writer = (BIStringNIOWriter) discovery.getCubeWriter(locationW);
                    writer.recordSpecificValue(0, "r");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    writer.clear();
                }
            }
        };
        Runnable clearWriteTask = new Runnable() {
            @Override
            public void run() {
                BIStringNIOWriter writer = null;
                String r = "";
                try {
                    writer = (BIStringNIOWriter) discovery.getCubeWriter(locationW);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    writer.clear();
                }
            }
        };
        Runnable forceReleaseWriteTask = new Runnable() {
            @Override
            public void run() {
                BIStringNIOWriter writer = null;
                String r = "";
                try {
                    writer = (BIStringNIOWriter) discovery.getCubeWriter(locationW);
                    Thread.sleep((Thread.currentThread().getId() % 5 * 2000));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    writer.forceRelease();
                }
            }
        };


        for (int i = 0; i < 10; i++) {
            new Thread(readTask).start();
        }
        for (int i = 0; i < 5; i++) {
            new Thread(clearReadTask).start();
        }
        for (int i = 0; i < 5; i++) {
            new Thread(forceReleaseReadTask).start();
        }

//        for (int i = 0; i < 5; i++) {
//            new Thread(writeTask).start();
//        }

    }
}
