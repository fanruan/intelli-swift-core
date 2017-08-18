package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.mem.BIReleaseUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by 小灰灰 on 2016/9/30.
 */
public abstract class BIBaseSingleFileNIOReader extends BIAbstractBaseNIOReader {

    protected MappedByteBuffer buffer;
    protected FileChannel fc;

    public BIBaseSingleFileNIOReader(File cacheFile) {
        super(cacheFile);
    }

    public BIBaseSingleFileNIOReader(String cacheFilePath) {
        this(new File(cacheFilePath));
    }


    protected abstract void releaseChild();

    protected void initBuffer() {
        if (isValid) {
            /**
             * 资源不可用，需要初始化，释放读锁，加写锁。
             */
            readWriteLock.writeLock().lock();
            try {
                if (buffer != null || !isValid) {
                    return;
                }
                fc = initFile();
                buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                initChild(buffer);
            } catch (IOException e) {
                BILoggerFactory.getLogger(BIBaseSingleFileNIOReader.class).error(e.getMessage(), e);
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

    public void releaseBuffer() {
        if (buffer != null) {
            BIReleaseUtils.doClean(buffer);
            buffer.clear();
            buffer = null;
        }
    }

    private void releaseChannel() throws IOException {
        if (fc != null) {
            fc.close();
            fc = null;
        }
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
}
