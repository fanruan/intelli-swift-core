package com.finebi.cube.data.disk.reader;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.disk.NIOHandlerManager;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wang on 2016/9/30.
 */
public class ReaderHandlerManager implements NIOHandlerManager<ICubePrimitiveReader> {
    private ICubePrimitiveReader reader = null;
    protected AtomicInteger countOfReaders = new AtomicInteger(0);
    protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected volatile boolean isForceReleased = false;
    private ICubeResourceLocation resourceLocation = null;
    private CopyOnWriteArrayList<String> queryRecorder = new CopyOnWriteArrayList<String>();

    public ReaderHandlerManager(ICubePrimitiveReader reader) {
        this.reader = reader;
    }

    public ReaderHandlerManager(ICubePrimitiveReader reader, ICubeResourceLocation resourceLocation) {
        this.reader = reader;
        this.resourceLocation = resourceLocation;
    }

    @Override
    public ICubePrimitiveReader queryHandler() {
        try {
            readWriteLock.readLock().lock();
            countOfReaders.incrementAndGet();
            if(BILoggerFactory.getLogger().isDebugEnabled()) {
                BILoggerFactory.getLogger().debug(" query reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
            }
            return reader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void releaseHandler(String handlerKey) {
        try {
            readWriteLock.writeLock().lock();
            int currentCount = -1;
            if (queryRecorder.contains(handlerKey)) {
                queryRecorder.remove(handlerKey);
                countOfReaders.decrementAndGet();
            } else {
                if(BILoggerFactory.getLogger().isDebugEnabled()) {
                    BILoggerFactory.getLogger().debug(handlerKey + "reader has been released============ " + resourceLocation.getAbsolutePath());
                }
            }
            currentCount = countOfReaders.get();
            if (currentCount == 0) {
                try {
                    isForceReleased = true;
                    reader.releaseSource();
                    countOfReaders.set(0);
                    queryRecorder.clear();
                    if(BILoggerFactory.getLogger().isDebugEnabled()) {
                        BILoggerFactory.getLogger().debug("count=0 release reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
                    }
                } catch (Exception e) {
                    throw BINonValueUtils.beyondControl(e);
                } finally {
                    isForceReleased = false;
                }
            } else if (currentCount < 0) {
                countOfReaders.set(0);
                if(BILoggerFactory.getLogger().isDebugEnabled()) {
                    BILoggerFactory.getLogger().debug("count<0 reset count reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
                }
            } else {
                if(BILoggerFactory.getLogger().isDebugEnabled()) {
                    BILoggerFactory.getLogger().debug("count>0 reduce count of reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
                }
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void destroyHandler() {
        try {
            readWriteLock.writeLock().lock();
            isForceReleased = true;
            reader.reSetValid(false);
            reader.destroySource();
            countOfReaders.set(0);
            queryRecorder.clear();
            if(BILoggerFactory.getLogger().isDebugEnabled()) {
                BILoggerFactory.getLogger().debug("force release reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        } finally {
            try {
                if (!BICubeDiskPrimitiveDiscovery.getInstance().isReleasingResource()) {
                    reValidHandler();
                }
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }
    }

    @Override
    public boolean isHandlerEmpty() {
        return countOfReaders.get() == 0;
    }

    @Override
    public void registerHandlerKey(String handlerKey) {
        queryRecorder.add(handlerKey);
    }

    @Override
    public boolean isForceReleased() {
        try {
            readWriteLock.readLock().lock();
            return isForceReleased;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void reValidHandler() {
        try {
            readWriteLock.writeLock().lock();
            isForceReleased = false;
            reader.reSetValid(true);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void inValidHandler() {
        try {
            readWriteLock.writeLock().lock();
            reader.reSetValid(false);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
