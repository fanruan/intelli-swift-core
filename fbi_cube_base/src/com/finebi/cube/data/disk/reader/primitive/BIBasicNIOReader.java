package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.stable.io.newio.NIOConstant;
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

public abstract class BIBasicNIOReader extends BIAbstractBaseNIOReader {

    protected Map<Integer, MappedByteBuffer> buffers = new ConcurrentHashMap<Integer, MappedByteBuffer>();
    protected transient MappedByteBuffer[] bufferArray = new MappedByteBuffer[1];
    protected Map<Integer, FileChannel> fcMap = new ConcurrentHashMap<Integer, FileChannel>();

    public BIBasicNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public BIBasicNIOReader(String cacheFilePath) {
        this(new File(cacheFilePath));
    }

    protected abstract long getPageModeValue();

    protected abstract long getPageStep();

    protected abstract void releaseChild();

    protected int getPage(long filePosition) throws BIResourceInvalidException {
        if (filePosition < 0) {
            throw BINonValueUtils.illegalArgument("The value of argument must be positive,but it's " + filePosition + " now");
        }
        if (isValid && (!BICubeDiskPrimitiveDiscovery.getInstance().isReleasingResource())) {
            int index = (int) (filePosition >> getPageStep() >> NIOConstant.MAX_SINGLE_FILE_PART_SIZE);
            initBuffer(index);
            return index;
        } else {
            throw new BIResourceInvalidException("resource invalid");
        }
    }

    protected int getIndex(long filePosition) {
        return (int) (filePosition & getPageModeValue());
    }

    private boolean buffersContains(int index) {
        if (bufferArray.length <= index) {
            synchronized (this) {
                if (bufferArray.length <= index) {
                    MappedByteBuffer[] temp = new MappedByteBuffer[index + 1];
                    System.arraycopy(bufferArray, 0, temp, 0, bufferArray.length);
                    bufferArray = temp;
                }
            }
            return false;
        }
        return bufferArray[index] != null;
    }

    private void initBuffer(int index) {
        if (!buffersContains(index)) {
            /**
             * 资源不可用，需要初始化，释放读锁，加写锁。
             */
            readWriteLock.writeLock().lock();
            try {
                /**
                 * 重新检查是否有其它线程已经初始化了
                 */
                if (!buffersContains(index)) {
                    initialBuffer(index);
                }
                /**
                 * 添加读锁
                 */
            } catch (IOException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            } finally {
                /**
                 * 释放写锁，保持读锁
                 */
                readWriteLock.writeLock().unlock();
            }
        }
    }

    protected void unMap() throws IOException {
        releaseChild();
        releaseBuffer();
        releaseChannel();
    }

    @Override
    protected void setBufferInValid() {

    }

    public void releaseBuffer() {
        for (Entry<Integer, MappedByteBuffer> entry : buffers.entrySet()) {
            BIReleaseUtils.doClean(entry.getValue());
        }
        buffers.clear();
        bufferArray = new MappedByteBuffer[1];
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

    private void initialBuffer(int index) throws IOException {
        FileChannel channel = initFile(index);
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        initChild(index, buffer);
        buffers.put(index, buffer);
        bufferArray[index] = buffer;
        fcMap.put(index, channel);
    }

    protected abstract void initChild(int index, MappedByteBuffer buffer);

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
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

}