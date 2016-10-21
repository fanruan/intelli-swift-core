package com.finebi.cube.data.disk.reader;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.disk.NIOHandlerManager;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wang on 2016/9/30.
 */
public class ReaderHandlerManager implements NIOHandlerManager<ICubePrimitiveReader> {
    ICubePrimitiveReader reader = null;
    protected AtomicInteger countOfReaders = new AtomicInteger(0);
    protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected boolean isForceReleased = false;
    ICubeResourceLocation resourceLocation = null;

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
            BILoggerFactory.getLogger().debug(" query reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
            return reader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void releaseHandler() {
        try {
            readWriteLock.writeLock().lock();
            int currentCount = countOfReaders.decrementAndGet();
            if (currentCount == 0) {
                try {
                    isForceReleased = true;
                    reader.releaseSource();
                    countOfReaders.set(0);
                    BILoggerFactory.getLogger().debug("count=0 release reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
                } catch (Exception e) {

                } finally {
                    isForceReleased = false;
                }
            } else if (currentCount < 0) {
                countOfReaders.set(0);
                BILoggerFactory.getLogger().debug("count<0 reset count reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
            } else {
                BILoggerFactory.getLogger().debug("count>0 reduce count of reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void forceReleaseHandler() {
        try {
            readWriteLock.writeLock().lock();
            isForceReleased = true;
            reader.destroySource();
            countOfReaders.set(0);
            BILoggerFactory.getLogger().debug("force release reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        } finally {
            isForceReleased = false;
            reader.reSetValid(true);
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isHandlerEmpty() {
        return countOfReaders.get() == 0;
    }

    @Override
    public void reSetHandlerValid(boolean isValid) {
        reader.reSetValid(isValid);
    }

    @Override
    public void printCountOfHandler() {
        if (countOfReaders.get() != 0) {
            BILoggerFactory.getLogger().debug("count reader " + resourceLocation.getAbsolutePath() + " " + countOfReaders.get());
        }
    }


    @Override
    public boolean isForceReleased() {
        return isForceReleased;
    }

    public void setForceReleased(boolean isForce) {
        isForceReleased = isForce;
    }
}
