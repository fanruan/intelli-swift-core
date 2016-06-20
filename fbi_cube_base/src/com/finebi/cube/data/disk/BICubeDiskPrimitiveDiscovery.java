package com.finebi.cube.data.disk;

import com.finebi.cube.data.BICubeReleaseRecorder;
import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.cache.BIResourceSimpleCache;
import com.finebi.cube.data.disk.reader.primitive.BIPrimitiveNIOReaderManager;
import com.finebi.cube.data.disk.writer.primitive.BIPrimitiveNIOWriterManager;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.common.factory.BIFactoryHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */

public class BICubeDiskPrimitiveDiscovery implements ICubePrimitiveResourceDiscovery {
    private BIPrimitiveNIOWriterManager writerManager;
    private BIPrimitiveNIOReaderManager readerManager;
    private static BICubeDiskPrimitiveDiscovery instance;
    private Map<ICubeResourceLocation, ResourceLock> resourceLockMap;
    private BIResourceSimpleCache<ICubePrimitiveReader> readerCache;
    private BIResourceSimpleCache<ICubePrimitiveWriter> writerCache;

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
        writerCache = new BIResourceSimpleCache<ICubePrimitiveWriter>();
        readerCache = new BIResourceSimpleCache<ICubePrimitiveReader>();
    }

    @Override
    public ICubePrimitiveReader getCubeReader(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildReaderException {
        BICubeReleaseRecorder releaseRecorder = BIFactoryHelper.getObject(BICubeReleaseRecorder.class);
        if (readerCache.isAvailableResource(resourceLocation) && !readerCache.getResource(resourceLocation).isForceReleased()) {
            return readerCache.getResource(resourceLocation);
        } else {
            ICubePrimitiveReader reader = readerManager.buildCubeReader(resourceLocation);
            releaseRecorder.record(reader);
            reader.setReleaseHelper(releaseRecorder);
            readerCache.makeAvailable(resourceLocation, reader);
            return reader;
        }
    }

    @Override
    public ICubePrimitiveWriter getCubeWriter(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildWriterException {
        ResourceLock lock = getLock(resourceLocation);
        synchronized (lock) {
            if (writerCache.isAvailableResource(resourceLocation) && !writerCache.getResource(resourceLocation).isForceReleased()) {
                return writerCache.getResource(resourceLocation);
            } else {
                BICubeReleaseRecorder releaseRecorder = BIFactoryHelper.getObject(BICubeReleaseRecorder.class);
                ICubePrimitiveWriter writer = writerManager.buildCubeWriter(resourceLocation);
                releaseRecorder.record(writer);
                writer.setReleaseManager(releaseRecorder);
                writerCache.makeAvailable(resourceLocation, writer);
                return writer;
            }
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

    public void forceRelease() {
        readerCache.forceRelease();
        writerCache.forceRelease();
    }
}
