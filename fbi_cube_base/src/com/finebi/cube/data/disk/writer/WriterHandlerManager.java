package com.finebi.cube.data.disk.writer;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.BICubeReleaseRecorder;
import com.finebi.cube.data.disk.NIOHandlerManager;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wang on 2016/9/30.
 * 应该只能有一个线程在操作写，
 */
public class WriterHandlerManager implements NIOHandlerManager<ICubePrimitiveWriter> {
    ICubePrimitiveWriter writer = null;
    protected AtomicInteger countOfWriters = new AtomicInteger(0);
    protected boolean isForceReleased = false;
    ICubeResourceLocation resourceLocation = null;

    public WriterHandlerManager(ICubePrimitiveWriter writer) {
        this.writer = writer;
    }

    public WriterHandlerManager(ICubePrimitiveWriter writer, ICubeResourceLocation resourceLocation) {
        this.writer = writer;
        this.resourceLocation = resourceLocation;
    }

    @Override
    public ICubePrimitiveWriter queryHandler() {
        synchronized (this) {
            countOfWriters.getAndIncrement();
            BILoggerFactory.getLogger().debug("query writer " + resourceLocation.getAbsolutePath().substring(50) + " " + countOfWriters.get());
            return writer;
        }
    }

    @Override
    public void releaseHandler() {
        synchronized (this) {
            int currentCountOfWriters = countOfWriters.decrementAndGet();
            if (currentCountOfWriters == 0) {
                try {
                    isForceReleased = true;
                    writer.releaseSource();
                    countOfWriters.set(0);
                    BILoggerFactory.getLogger().debug("=0 release writer " + resourceLocation.getAbsolutePath() + " " + countOfWriters.get());
                } catch (Exception e) {
                } finally {
                    isForceReleased = false;
                }
            } else if (currentCountOfWriters < 0) {
                countOfWriters.set(0);
                BILoggerFactory.getLogger().debug("<0 release writer " + resourceLocation.getAbsolutePath() + " " + countOfWriters.get());
            }
        }
    }

    @Override
    public void forceReleaseHandler() {
        synchronized (this) {
            isForceReleased = true;
            writer.destoryResource();
            countOfWriters.set(0);
            BILoggerFactory.getLogger().debug("force release writer " + resourceLocation.getAbsolutePath() + " " + countOfWriters.get());
        }
    }

    @Override
    public boolean isHandlerEmpty() {
        return countOfWriters.get() == 0;
    }

    @Override
    public void printCountOfHandler() {
        if (countOfWriters.get() != 0) {
            BILoggerFactory.getLogger().debug("count reader " + resourceLocation.getAbsolutePath() + " " + countOfWriters.get());
        }
    }

    @Override
    public boolean isForceReleased() {
        return isForceReleased;
    }

    @Override
    public void reSetHandlerValid(boolean isValid) {
//        writer.reSetValid(isValid);
//        writer不应该重置，因为需要调用构造方法中的文件创建方法
    }

    public void setForceReleased(boolean isForce) {
        this.isForceReleased = isForce;
    }
}
