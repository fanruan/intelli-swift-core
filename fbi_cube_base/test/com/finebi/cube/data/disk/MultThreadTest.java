package com.finebi.cube.data.disk;

import com.finebi.cube.data.disk.reader.BIStringNIOReader;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.tools.BILocationBuildTestTool;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by wang on 2016/10/9.
 */
public class MultThreadTest {
    public static void main(String[] args) {
        final ICubeResourceLocation location = BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "writer" + 0+ "ok");
        location.setStringType();
        location.setReaderSourceLocation();
        final BICubeDiskDiscovery discovery = BICubeDiskDiscovery.getInstance();

        Runnable readTask = new Runnable() {
            @Override
            public void run() {
                int filesize = 1;
                int size = 102400000;
                String r = "";
                BIStringNIOReader reader = null;
                try {
                    reader = (BIStringNIOReader) discovery.getCubeReader(location);
                } catch (IllegalCubeResourceLocationException e) {
                    e.printStackTrace();
                } catch (BIBuildReaderException e) {
                    e.printStackTrace();
                }
                try {
                    for (int i = 0; i < filesize; i++) {
                        for (int j = 0; j < size; j++) {
                            r = reader.getSpecificValue(j);
                            if(j%50000000==0){
                                System.err.println("read "+j+ " "+r);
                            }
                        }
                        reader.clear();
                    }
                    System.out.println("finish...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable clearTask = new Runnable() {
            @Override
            public void run() {

                BIStringNIOReader reader = null;
                try {
                    reader = (BIStringNIOReader) discovery.getCubeReader(location);
                } catch (IllegalCubeResourceLocationException e) {
                    e.printStackTrace();
                } catch (BIBuildReaderException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(3000*(Thread.currentThread().getId()%4+1));
                    System.out.println(" others start clear...");
                    reader.forceRelease();
//                    reader.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        for (int i = 0; i < 4; i++) {
            new Thread(readTask).start();
        }

        for (int i = 0; i < 4; i++) {
            new Thread(clearTask).start();
        }
    }
}
