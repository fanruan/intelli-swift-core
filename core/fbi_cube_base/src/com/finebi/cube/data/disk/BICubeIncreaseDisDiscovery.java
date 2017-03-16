package com.finebi.cube.data.disk;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.disk.reader.BINIOReaderManager;
import com.finebi.cube.data.disk.writer.BINIOWriterIncreaseManager;
import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.output.ICubeWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * Created by kary on 2016/8/7.
 */
public class BICubeIncreaseDisDiscovery implements ICubeResourceDiscovery {

    private BINIOWriterIncreaseManager writerManager;
    private BINIOReaderManager readerManager;
    private static BICubeIncreaseDisDiscovery instance;

    public static BICubeIncreaseDisDiscovery getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (BICubeIncreaseDisDiscovery.class) {
                if (instance == null) {
                    instance = new BICubeIncreaseDisDiscovery();
                }
                return instance;
            }
        }
    }

    private BICubeIncreaseDisDiscovery() {
        writerManager = BINIOWriterIncreaseManager.getInstance();
        readerManager = BINIOReaderManager.getInstance();
    }

    @Override
    public ICubeReader getCubeReader(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildReaderException {
        return readerManager.buildCubeReader(resourceLocation);
    }

    @Override
    public ICubeWriter getCubeWriter(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildWriterException {
        return writerManager.buildCubeWriter(resourceLocation);
    }

    @Override
    public boolean isResourceExist(ICubeResourceLocation resourceLocation) {
        File file = new File(resourceLocation.getAbsolutePath());
        return file.exists();
    }
}
