package com.finebi.cube.data.disk;

import com.finebi.cube.data.disk.reader.BICubeIntegerReaderWrapper;
import com.finebi.cube.data.disk.reader.BIStringNIOReader;
import com.finebi.cube.data.disk.writer.BIStringNIOWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.tools.BILocationBuildTestTool;
import com.fr.bi.stable.utils.file.BIFileUtils;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

/**
 * Created by wang on 2016/10/9.
 */
public class NIOResourceManagerTest extends TestCase {
    public static BICubeIntegerReaderWrapper reader1 = null;
    public void testReadAndRead() {
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

    public void testReadAndWrite() {
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
            writer1.recordSpecificValue(1, "b");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public void testWriteAndRead() {
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
            writer1.recordSpecificValue(1, "b");
//            writer1.forceRelease();
            reader1 = (BIStringNIOReader) discovery.getCubeReader(locationR);
            r = reader1.getSpecificValue(0);
//            reader1.forceRelease();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public void testWriteAndWrite() {
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
            writer1.recordSpecificValue(1, "b");
            writer1.forceRelease();
            writer2 = (BIStringNIOWriter) discovery.getCubeWriter(locationW);
            writer2.recordSpecificValue(1, "b");
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

    public void testAllRelease() {
        String basePath = "D:\\bigfiles\\env\\WebReport\\WEB-INF\\testFolder\\cube";
        final BICubeDiskDiscovery discovery = BICubeDiskDiscovery.getInstance();
        final int size = 10240;
        Runnable forceRelease = new Runnable() {
            @Override
            public void run() {
                sleepy();
                BICubeDiskPrimitiveDiscovery.getInstance().forceRelease();
                BICubeDiskPrimitiveDiscovery.getInstance().finishRelease();
                System.out.println("finish clear");
            }
        };
        for(int i=0;i<4;i++){
            final ICubeResourceLocation locationRR = BILocationBuildTestTool.buildWrite(basePath, "test" + i + "ok");
            locationRR.setStringType();
            locationRR.setReaderSourceLocation();
            final int index = i;
            Runnable readRR = new Runnable() {
                @Override
                public void run() {
                    sleepy();
                    readData(size*100, discovery, locationRR);
                    System.err.println("finish read test "+ index);
                }
            };
            new Thread(readRR).start();
        }
        new Thread(forceRelease).start();

        for (int i=0;i<4;i++){
            final ICubeResourceLocation locationW = BILocationBuildTestTool.buildWrite(basePath, "testww" + i + "ok");
            locationW.setStringType();
            locationW.setWriterSourceLocation();
            final int index = i;
            Runnable writeW = new Runnable() {
                @Override
                public void run() {
                    sleepy();
                    writeData(size, discovery, locationW);
                    System.err.println("finish write "+ index);
                }
            };
            new Thread(writeW).start();
        }
        for(int i=0;i<4;i++){
            final ICubeResourceLocation locationRR = BILocationBuildTestTool.buildWrite(basePath, "testw" + i + "ok");
            locationRR.setStringType();
            locationRR.setReaderSourceLocation();
            final int index = i;
            Runnable readRR = new Runnable() {
                @Override
                public void run() {
                    sleepy();
                    readData(size*100, discovery, locationRR);
                    System.err.println("finish read test "+ index);
                }
            };
            new Thread(readRR).start();
        }
//        new Thread(forceRelease).start();

        for(int i=0;i<4;i++){
            final ICubeResourceLocation locationRR = BILocationBuildTestTool.buildWrite(basePath, "testww" + i + "ok");
            locationRR.setStringType();
            locationRR.setReaderSourceLocation();
            final int index = i;
            Runnable readRR = new Runnable() {
                @Override
                public void run() {
                    sleepy();
                    readData(size, discovery, locationRR);
                    System.err.println("finish read testww "+ index);
                }
            };
            new Thread(readRR).start();
        }



        System.out.println("------------");
        forceRelease.run();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepy() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void writeData(int size, BICubeDiskDiscovery discovery, ICubeResourceLocation locationW) {
        BIStringNIOWriter writer = null;
        try {
            writer = (BIStringNIOWriter) discovery.getCubeWriter(locationW);
            for (int i = 0; i < size/100; i++) {
                writer.recordSpecificValue(i, "还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧还是用中" +
                        "还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧文字符串测试会比较长一点吧" +
                        "还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧" +
                        "还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧" +
                        "还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧" +
                        "还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧" +
                        "还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧" +
                        "还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧还是用中文字符串测试会比较长一点吧");
            }
        } catch (IllegalCubeResourceLocationException e) {
            e.printStackTrace();
        } catch (BIBuildWriterException e) {
            e.printStackTrace();
        } finally {
            writer.forceRelease();
        }
    }

    private void readData(int size, BICubeDiskDiscovery discovery, ICubeResourceLocation locationR) {
        BIStringNIOReader reader = null;
        try {
            reader = (BIStringNIOReader) discovery.getCubeReader(locationR);
            for(int i=0;i<size;i++){
                if(i%10000==0) {
                    String tmp = reader.getSpecificValue(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(100);
                reader = (BIStringNIOReader) discovery.getCubeReader(locationR);
                for(int i=0;i<size;i++){
                    if(i%10000==0) {
                        String tmp = reader.getSpecificValue(i);
                    }
                }
            } catch (IllegalCubeResourceLocationException e1) {
                e1.printStackTrace();
            } catch (BIBuildReaderException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (BIResourceInvalidException e1) {
                e1.printStackTrace();
            }
        } finally {
            System.out.println("finish reading..");
            reader.clear();
        }
    }
    private void queryReader(BICubeDiskDiscovery discovery, ICubeResourceLocation locationR) {
        BICubeIntegerReaderWrapper reader = null;
        try {
            reader = (BICubeIntegerReaderWrapper) discovery.getCubeReader(locationR);
            for(int i=0;i<1;i++){
                System.out.println("---"+reader.getSpecificValue(i));
            }
            if(NIOResourceManagerTest.reader1==null){
                NIOResourceManagerTest.reader1 = reader;
            }
        } catch (Exception e) {
        } finally {
            System.out.println("finish reading..");
//            模拟前端调用不释放的情况
//            reader.clear();
        }
    }
    private void queryWriter(BICubeDiskDiscovery discovery, ICubeResourceLocation locationW) {
        BIStringNIOWriter writer = null;
        try {
            writer = (BIStringNIOWriter) discovery.getCubeWriter(locationW);
            for (int i = 0; i < 100; i++) {
                writer.recordSpecificValue(i, "a");
            }
        } catch (IllegalCubeResourceLocationException e) {
            e.printStackTrace();
        } catch (BIBuildWriterException e) {
            e.printStackTrace();
        } finally {
            writer.forceRelease();
        }
    }

    public void testQueryHandler(){
        String basePath = "D:\\bigfiles\\env\\WebReport\\WEB-INF\\testFolder\\cube";
        final BICubeDiskDiscovery discovery = BICubeDiskDiscovery.getInstance();
        for(int i=0;i<2;i++){
            final ICubeResourceLocation locationRR = BILocationBuildTestTool.buildWrite(basePath, "query" + 0+".fp");
            locationRR.setIntegerTypeWrapper();
            locationRR.setReaderSourceLocation();
            final int index = i;
            Runnable readRR = new Runnable() {
                @Override
                public void run() {
                    sleepy();
                    queryReader(discovery, locationRR);
                    System.err.println("finish read testww "+ index);
                }
            };
            new Thread(readRR).start();
        }
        NIOResourceManagerTest.reader1.clear();
        try {
            NIOResourceManagerTest.reader1.getSpecificValue(0);
        } catch (BIResourceInvalidException e) {
            e.printStackTrace();
        }
        BICubeDiskPrimitiveDiscovery.getInstance().forceRelease();
        BICubeDiskPrimitiveDiscovery.getInstance().finishRelease();
        System.out.println("--------------------");
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
