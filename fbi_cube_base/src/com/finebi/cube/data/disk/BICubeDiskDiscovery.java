package com.finebi.cube.data.disk;

import com.finebi.cube.data.disk.reader.BINIOReaderManager;
import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.output.ICubeWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.disk.writer.BINIOWriterManager;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.common.factory.annotation.BISingletonObject;

/**
 * This class created on 2016/3/16.
 *
 * @author Connery
 * @since 4.0
 */

@BIMandatedObject(module = IModuleFactory.CUBE_BASE_MODULE, factory = BIMateFactory.CUBE_BASE
        , implement = ICubeResourceDiscovery.class)
@BISingletonObject
public class BICubeDiskDiscovery implements ICubeResourceDiscovery {


    private BINIOWriterManager writerManager;
    private BINIOReaderManager readerManager;
    private static BICubeDiskDiscovery instance;

    public static BICubeDiskDiscovery getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (BICubeDiskDiscovery.class) {
                if (instance == null) {
                    instance = new BICubeDiskDiscovery();
                }
                return instance;
            }
        }
    }

    private BICubeDiskDiscovery() {
        writerManager = BINIOWriterManager.getInstance();
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
}
