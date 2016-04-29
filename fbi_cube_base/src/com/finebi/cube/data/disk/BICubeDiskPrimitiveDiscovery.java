package com.finebi.cube.data.disk;

import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.disk.reader.primitive.BIPrimitiveNIOReaderManager;
import com.finebi.cube.data.disk.writer.primitive.BIPrimitiveNIOWriterManager;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.common.factory.annotation.BISingletonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BASE_MODULE, factory = BIMateFactory.CUBE_BASE
        , implement = ICubePrimitiveResourceDiscovery.class)
@BISingletonObject
public class BICubeDiskPrimitiveDiscovery implements ICubePrimitiveResourceDiscovery {
    private BIPrimitiveNIOWriterManager writerManager;
    private BIPrimitiveNIOReaderManager readerManager;
    private static BICubeDiskPrimitiveDiscovery instance;
    private Map<ICubeResourceLocation, ResourceLock> resourceLockMap;

    public static BICubeDiskPrimitiveDiscovery getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (BIPrimitiveNIOWriterManager.class) {
                if (instance == null) {
                    instance = new BICubeDiskPrimitiveDiscovery();
                }
                return instance;
            }
        }
    }

    private BICubeDiskPrimitiveDiscovery() {
        writerManager = BIPrimitiveNIOWriterManager.getInstance();
        readerManager = BIPrimitiveNIOReaderManager.getInstance();
        resourceLockMap = new HashMap<ICubeResourceLocation, ResourceLock>();
    }

    @Override
    public ICubePrimitiveReader getCubeReader(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildReaderException {
        return readerManager.buildCubeReader(resourceLocation);
    }

    @Override
    public ICubePrimitiveWriter getCubeWriter(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildWriterException {
        ResourceLock lock = getLock(resourceLocation);
        synchronized (lock) {
            return writerManager.buildCubeWriter(resourceLocation);
        }
    }

    private ResourceLock getLock(ICubeResourceLocation resourceLocation) {
        if (resourceLockMap.containsKey(resourceLocation)) {
            return resourceLockMap.get(resourceLocation);
        } else {
            synchronized (resourceLockMap) {
                if (!resourceLockMap.containsKey(resourceLocation)) {
                    ResourceLock lock = new ResourceLock();
                    resourceLockMap.put(resourceLocation, lock);
                }
                return resourceLockMap.get(resourceLocation);
            }
        }
    }

    private class ResourceLock {

    }
}
