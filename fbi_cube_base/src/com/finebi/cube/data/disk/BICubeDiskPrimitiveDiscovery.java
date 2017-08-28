package com.finebi.cube.data.disk;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */

public class BICubeDiskPrimitiveDiscovery implements ICubePrimitiveResourceDiscovery {

    private static BICubeDiskPrimitiveDiscovery instance = new BICubeDiskPrimitiveDiscovery();
    private Map<ICubeResourceLocation, ResourceLock> resourceLockMap;
    private Map<String, NIOResourceManager> fileResourceMap;
    private volatile boolean releasingResource = false;

    public boolean isReleasingResource() {
        return releasingResource;
    }

    public static BICubeDiskPrimitiveDiscovery getInstance() {
        return instance;
    }

    private BICubeDiskPrimitiveDiscovery() {
        resourceLockMap = new ConcurrentHashMap<ICubeResourceLocation, ResourceLock>();
        fileResourceMap = new ConcurrentHashMap<String, NIOResourceManager>();
    }

    @Override
    public ICubePrimitiveReader getCubeReader(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildReaderException {
        String filePath = resourceLocation.getAbsolutePath();
        synchronized (this) {
            if (releasingResource) {
                throw new RuntimeException("Current can't get the resource reader");
            }
            if (!fileResourceMap.containsKey(filePath)) {
                NIOResourceManager nioResourceManager = new NIOResourceManager();
                fileResourceMap.put(filePath, nioResourceManager);
            }
            NIOResourceManager nioReaderManager = fileResourceMap.get(filePath);
            try {
                nioReaderManager.getReadWriteLock().readLock().lock();
                return nioReaderManager.getCubeReader(resourceLocation);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e);
            } finally {
                nioReaderManager.getReadWriteLock().readLock().unlock();
            }

        }
    }

    @Override
    public ICubePrimitiveWriter getCubeWriter(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildWriterException {
        String filePath = resourceLocation.getAbsolutePath();
        synchronized (this) {
            if (releasingResource) {
                throw new RuntimeException("Current can't get the resource writer");
            }
            if (!fileResourceMap.containsKey(filePath)) {
                NIOResourceManager nioResourceManager = new NIOResourceManager();
                fileResourceMap.put(filePath, nioResourceManager);
            }
            NIOResourceManager nioWriterManager = fileResourceMap.get(filePath);
            try {
                nioWriterManager.getReadWriteLock().writeLock().lock();
                return nioWriterManager.getCubeWriter(resourceLocation);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e);
            } finally {
                nioWriterManager.getReadWriteLock().writeLock().unlock();
            }
        }
    }

    private ResourceLock getLock(ICubeResourceLocation resourceLocation) {
        /**
         * 加强一些
         */
        synchronized (resourceLockMap) {
            if (resourceLockMap.containsKey(resourceLocation)) {
                return resourceLockMap.get(resourceLocation);
            } else {
                if (!resourceLockMap.containsKey(resourceLocation)) {
                    ResourceLock lock = new ResourceLock();
                    resourceLockMap.put(resourceLocation, lock);
                }
                return resourceLockMap.get(resourceLocation);
            }
        }
    }

    private boolean isAvailable(NIOHandlerManager nioHandler) {
        return nioHandler != null;
    }

    public List<String> getUnReleasedLocation() {
        synchronized (this) {
            List<String> locations = new ArrayList<String>();
            try {
                for (Map.Entry<String, NIOResourceManager> entry : fileResourceMap.entrySet()) {
                    NIOHandlerManager reader = entry.getValue().getReaderHandlerManager();
                    if (isAvailable(reader) && (!reader.isHandlerEmpty())) {
                        locations.add(entry.getKey() + "-reader");
                    }
                    NIOHandlerManager writer = entry.getValue().getWriterHandlerManager();
                    if (isAvailable(writer) && !writer.isHandlerEmpty()) {
                        locations.add(entry.getKey() + "-writer");
                    }
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
            return locations;
        }
    }

    private class ResourceLock {

    }

    public void finishRelease() {
        synchronized (this) {
            try {
                for (NIOResourceManager nioManager : fileResourceMap.values()) {
                    nioManager.reValidReader();
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            } finally {
                releasingResource = false;
            }
        }
    }


    public void clearFileNotExist(String fileKey) {
        if (fileResourceMap.containsKey(fileKey)) {
            if (!new File(fileKey).exists()) {
                fileResourceMap.get(fileKey).forceRelease();
                fileResourceMap.remove(fileKey);
            }
        }
    }

    //    更改cube路径时候将旧的nioManager清除
    public void clearResourceMap() {
        synchronized (this) {
            fileResourceMap.clear();
        }
    }


    public void forceRelease() {
        synchronized (this) {
            releasingResource = true;
            try {
                for (NIOResourceManager nioManager : fileResourceMap.values()) {
                    nioManager.inValidReader();
                }
                for (NIOResourceManager nioManager : fileResourceMap.values()) {
                    nioManager.forceRelease();
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            } finally {
            }
        }
    }
}
