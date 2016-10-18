package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubeSourceReleaseManager;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.fr.bi.stable.utils.mem.BIReleaseUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by 小灰灰 on 2016/9/30.
 */
public abstract class BIBaseSingleFileNIOReader implements ICubePrimitiveReader {

    protected MappedByteBuffer buffer;
    protected FileChannel fc;
    protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected volatile boolean isValid = true;
    private ICubeSourceReleaseManager releaseManager;
    protected File baseFile;
    private String readerHandler;

    public BIBaseSingleFileNIOReader(File cacheFile) {
        this.baseFile = cacheFile;
        this.isValid = true;
        readerHandler = UUID.randomUUID().toString();
        initBuffer();
    }

    @Override
    public String getReaderHandler() {
        return readerHandler;
    }

    public BIBaseSingleFileNIOReader(String cacheFilePath) {
        this(new File(cacheFilePath));
    }


    protected abstract void releaseChild();

    private void initBuffer() {
        /**
         * 资源不可用，需要初始化，释放读锁，加写锁。
         */
        readWriteLock.writeLock().lock();
        try {
            fc = initFile();
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            initChild(buffer);
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        } finally {
            /**
             * 释放写锁，保持读锁
             */
            readWriteLock.writeLock().unlock();
        }
    }

    private boolean useReleaseManager() {
        return releaseManager != null;
    }
    @Override
    public void releaseHandler() {
        if (useReleaseManager()) {
            releaseManager.release(this);
        } else {
            releaseSource();
        }
    }

    @Override
    public void forceRelease() {
        releaseSource();
    }

    @Override
    public boolean isForceReleased() {
        return !isValid;
    }

    public void releaseSource() {
        readWriteLock.writeLock().lock();
        if (!isValid) {
            return;
        }
        isValid = false;
        try {
            //daniel:但愿1ms能 执行完get方法否则可能导致jvm崩溃锁太浪费资源了，1ms目前并没有遇到问题,最垃圾的磁盘也读完了
            Thread.currentThread().sleep(1);
        } catch (InterruptedException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        try {
            releaseChild();
            releaseBuffer();
            releaseChannel();
            BILoggerFactory.getLogger().info("===============single release reader==================");
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }


    private void releaseBuffer() {
        if(buffer!=null) {
            BIReleaseUtils.doClean(buffer);
//            buffer = null;
        }
    }

    private void releaseChannel() throws IOException {
        if (fc != null) {
            fc.close();
//            fc = null;
        }
    }

    @Override
    public void setReleaseHelper(ICubeSourceReleaseManager releaseHelper) {
        this.releaseManager = releaseHelper;
    }

    protected abstract void initChild(MappedByteBuffer buffer);

    private FileChannel initFile() {
        try {
            return new RandomAccessFile(baseFile, "r").getChannel();
        } catch (FileNotFoundException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean canReader() {
        return isValid && baseFile.exists() && baseFile.length() > 0;
    }
}
