package com.finebi.cube.data.disk.reader.primitive;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class BIBasicNIOReader<T> implements ICubePrimitiveReader<T> {

    private final static int INIT_INDEX_LENGTH = 128;
    protected Map<Long, MappedByteBuffer> buffers = new ConcurrentHashMap<Long, MappedByteBuffer>();
    protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected Map<Long, FileChannel> fcMap = new ConcurrentHashMap<Long, FileChannel>();
    boolean[] initIndex = new boolean[INIT_INDEX_LENGTH];
    private boolean isValid = true;
    private File baseFile;

    public BIBasicNIOReader(File cacheFile) {
        this.baseFile = cacheFile;
        this.isValid = true;
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
        readWriteLock.readLock().lock();
        if (!isValid) {
            readWriteLock.readLock().unlock();
            throw new BIResourceInvalidException();
        }
        Long index = filePosition >> getPageStep() >> NIOConstant.MAX_SINGLE_FILE_PART_SIZE;
        if (!buffers.containsKey(index)) {
            /**
             * 资源不可用，需要初始化，释放读锁，加写锁。
             */
            readWriteLock.readLock().unlock();
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
                readWriteLock.readLock().lock();
            } catch (IOException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            } finally {
                /**
                 * 释放写锁，保持读锁
                 */
                readWriteLock.writeLock().unlock();
            }

        }
        /**
         * 读取数据
         */
        try {
            return getValue(index, (int) (filePosition & getPageModeValue()));
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("the expect page value is:" + index, e);
        } finally {
            readWriteLock.readLock().unlock();
        }

    }

    protected abstract T getValue(Long page, int index);

    @Override
    public void clear() {
        readWriteLock.writeLock().lock();
        if (!isValid) {
            return;
        }
        isValid = false;
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
        return baseFile.exists() && baseFile.length() > 0;
    }
}