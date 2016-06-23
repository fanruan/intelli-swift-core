package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.ICubeSourceReleaseManager;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.mem.BIReleaseUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class BIBasicNIOReader<T> implements ICubePrimitiveReader<T> {

    private final static int INIT_INDEX_LENGTH = 128;
    protected Map<Long, MappedByteBuffer> buffers = new ConcurrentHashMap<Long, MappedByteBuffer>();
    protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected Map<Long, FileChannel> fcMap = new ConcurrentHashMap<Long, FileChannel>();
    boolean[] initIndex = new boolean[INIT_INDEX_LENGTH];
    protected volatile boolean isValid = true;
    private ICubeSourceReleaseManager releaseManager;
    private File baseFile;
    private String readerHandler;

    public BIBasicNIOReader(File cacheFile) {
        this.baseFile = cacheFile;
        this.isValid = true;
        readerHandler = UUID.randomUUID().toString();
    }

    @Override
    public String getReaderHandler() {
        return readerHandler;
    }

    public BIBasicNIOReader(String cacheFilePath) {
        this(new File(cacheFilePath));
    }

    protected abstract long getPageModeValue();

    protected abstract long getPageStep();

    protected abstract void releaseChild();

    @Override
    public T getSpecificValue(long filePosition) throws BIResourceInvalidException {
        if (filePosition < 0) {
            throw BINonValueUtils.illegalArgument("The value of argument must be positive,but it's " + filePosition + " now");
        }
        if(isValid) {
            Long index = filePosition >> getPageStep() >> NIOConstant.MAX_SINGLE_FILE_PART_SIZE;
            initBuffer(index);
            try {
                return getValue(index, (int) (filePosition & getPageModeValue()));
            } catch (IndexOutOfBoundsException e) {
                throw new RuntimeException("the expect page value is:" + index, e);
            } finally {
            }
        } else {
            throw new BIResourceInvalidException();
        }

    }

    private void initBuffer(Long index) {
        if (!buffers.containsKey(index)) {
            /**
             * 资源不可用，需要初始化，释放读锁，加写锁。
             */
            readWriteLock.writeLock().lock();
            try {
                /**
                 * 重新检查是否有其它线程已经初始化了
                 */
                if (!buffers.containsKey(index)) {
                    initialBuffer(index);
                }
                /**
                 * 添加读锁
                 */
            } catch (IOException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            } finally {
                /**
                 * 释放写锁，保持读锁
                 */
                readWriteLock.writeLock().unlock();
            }
        }
    }

    protected abstract T getValue(Long page, int index) throws BIResourceInvalidException;

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
        //先改变isValid状态再判断canClear
        isValid = false;
        try {
            //但愿10ms能 执行完get方法否则可能导致jvm崩溃
            //锁太浪费资源了，10ms目前并没有遇到问题
            this.wait(10);
        } catch (InterruptedException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        try {
            releaseChild();
            releaseBuffer();
            releaseChannel();
        } catch (IOException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    private boolean useReleaseManager() {
        return releaseManager != null;
    }

    private void releaseBuffer() {
        for (Entry<Long, MappedByteBuffer> entry : buffers.entrySet()) {
            BIReleaseUtils.doClean(entry.getValue());
        }
        buffers.clear();
    }

    private void releaseChannel() throws IOException {
        if (fcMap != null) {
            for (FileChannel fc : fcMap.values()) {
                if (fc != null) {
                    fc.close();
                }
            }
            fcMap.clear();
        }
    }

    private void initialBuffer(long index) throws IOException {
        FileChannel channel = initFile(index);
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        initChild(index, buffer);
        buffers.put(index, buffer);
        fcMap.put(index, channel);
    }

    @Override
    public void setReleaseHelper(ICubeSourceReleaseManager releaseHelper) {
        this.releaseManager = releaseHelper;
    }

    protected abstract void initChild(Long index, MappedByteBuffer buffer);

    private FileChannel initFile(long fileIndex) {
        //兼容之前的
        File cacheFile = null;
        if (fileIndex == 0) {
            cacheFile = baseFile;
        } else {
            cacheFile = new File(baseFile.getAbsolutePath() + "_" + fileIndex);
        }
        try {
            return new RandomAccessFile(cacheFile, "r").getChannel();
        } catch (FileNotFoundException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean canReader() {
        return isValid && baseFile.exists() && baseFile.length() > 0;
    }
}