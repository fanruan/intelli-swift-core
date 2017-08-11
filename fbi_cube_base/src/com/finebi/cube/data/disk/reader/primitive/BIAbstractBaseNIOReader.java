package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubeSourceReleaseManager;
import com.finebi.cube.data.disk.NIOHandlerManager;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.fr.bi.manager.PerformancePlugManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by 小灰灰 on 2016/10/26.
 */
public abstract class BIAbstractBaseNIOReader implements ICubePrimitiveReader {
    protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected volatile boolean isValid = true;
    private ICubeSourceReleaseManager releaseManager;
    private NIOHandlerManager nioHandlerManager;

    protected File baseFile;
    private String readerHandler;

    public BIAbstractBaseNIOReader(File cacheFile) {
        this.baseFile = cacheFile;
        this.isValid = true;
        readerHandler = UUID.randomUUID().toString();
    }

    @Override
    public String getReaderHandler() {
        return readerHandler;
    }

    public BIAbstractBaseNIOReader(String cacheFilePath) {
        this(new File(cacheFilePath));
    }

    @Override
    public void releaseHandler(String readerHandler) {
        if (useNioHandlerManager()) {
            nioHandlerManager.releaseHandler(readerHandler);
        } else {
            releaseSource();
        }
    }

    @Override
    public void forceRelease() {
        if (useNioHandlerManager()) {
            nioHandlerManager.destroyHandler();
        } else {
            destroySource();
        }
    }

    @Override
    public boolean isForceReleased() {
        return !isValid;
    }

    protected abstract void unMap() throws IOException;

    @Override
    public void destroySource() {
        try {
            readWriteLock.writeLock().lock();
            //再destroy之前必须在manager里面吧isvalid设置成false
            if (isValid == true) {
                BILoggerFactory.getLogger(this.getClass()).info("can not destroy valid reader");
            }
            setBufferInValid();
            try {
                //但愿10ms能 执行完get方法否则可能导致jvm崩溃
                //锁太浪费资源了，10ms目前并没有遇到问题
                //daniel:改成1ms，最垃圾的磁盘也读完了
                Thread.currentThread().sleep(PerformancePlugManager.getInstance().getCubeReaderReleaseSleepTime());
            } catch (InterruptedException e) {
                BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
            unMap();
        } catch (IOException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    protected abstract void setBufferInValid();

    @Override
    public void reSetValid(boolean isValid) {
        readWriteLock.writeLock().lock();
        this.isValid = isValid;
        readWriteLock.writeLock().unlock();
    }

    public void releaseSource() {
        if (PerformancePlugManager.getInstance().isUnmapReader()) {
            //如果isValid已经是false就不执行，并且不把isValid设成true
            try {
                readWriteLock.writeLock().lock();
                if (!isValid) {
                    return;
                }
            } finally {
                readWriteLock.writeLock().unlock();
            }
            try {
                readWriteLock.writeLock().lock();
                //先改变isValid状态再判断canClear
                isValid = false;
                setBufferInValid();
                try {
                    //但愿10ms能 执行完get方法否则可能导致jvm崩溃
                    //锁太浪费资源了，10ms目前并没有遇到问题
                    //daniel:改成1ms，最垃圾的磁盘也读完了
                    Thread.currentThread().sleep(PerformancePlugManager.getInstance().getCubeReaderReleaseSleepTime());
                } catch (InterruptedException e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }
                unMap();

            } catch (IOException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            } finally {
                isValid = true;
                readWriteLock.writeLock().unlock();
            }
        }
    }

    private boolean useReleaseManager() {
        return releaseManager != null;
    }

    private boolean useNioHandlerManager() {
        return nioHandlerManager != null;
    }

    @Override
    public void setReleaseHelper(ICubeSourceReleaseManager releaseHelper) {
        this.releaseManager = releaseHelper;
    }

    @Override
    public void setHandlerReleaseHelper(NIOHandlerManager releaseHelper) {
        this.nioHandlerManager = releaseHelper;
    }

    @Override
    public NIOHandlerManager getHandlerReleaseHelper() {
        return this.nioHandlerManager;
    }

    @Override
    public boolean canReader() {
        return isValid && baseFile.exists() && baseFile.length() > 0;
    }
}

