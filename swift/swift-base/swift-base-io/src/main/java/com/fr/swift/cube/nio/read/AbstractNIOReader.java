package com.fr.swift.cube.nio.read;

import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.cube.nio.NIOReadWriter;
import com.fr.swift.cube.nio.NIOReader;
import com.fr.swift.cube.nio.ReleaseUtils;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractNIOReader<T> extends NIOReadWriter implements NIOReader<T> {

    private final static int INIT_INDEX_LENGTH = 128;
    private static SwiftLogger LOGGER = SwiftLoggers.getLogger(AbstractNIOReader.class);
    protected Map<Long, MappedByteBuffer> buffers = new ConcurrentHashMap<Long, MappedByteBuffer>();
    protected Map<Long, FileChannel> fcMap = new ConcurrentHashMap<Long, FileChannel>();
    protected volatile boolean gotohell = false;
    boolean[] initIndex = new boolean[INIT_INDEX_LENGTH];
    private File baseFile;

    public AbstractNIOReader(File cacheFile) {
        this.baseFile = cacheFile;
    }

    protected abstract long getPageModeValue();

    @Override
    public T get(long pos) {
        if (gotohell) {
            return null;
        }
        Long index = pos >> getPageStep() >> NIOConstant.MAX_SINGLE_FILE_PART_SIZE;
        initBuffer(index);
        return getValue(index, (int) (pos & getPageModeValue()));
    }

    @Override
    public long getLastPos(long rowCount) {
        return rowCount;
    }

    protected abstract T getValue(Long index, int l);

    @Override
    public void release() {
        synchronized (this) {
            gotohell = true;
            try {
                //但愿10ms能 执行完get方法否则可能导致jvm崩溃
                this.wait(10);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
            clearBuffer();
        }
    }

    private void clearBuffer() {
        synchronized (this) {
            releaseChild();
            for (Entry<Long, MappedByteBuffer> entry : buffers.entrySet()) {
                ReleaseUtils.doClean(entry.getValue());
            }
            buffers.clear();
            try {
                if (fcMap != null) {
                    for (FileChannel fc : fcMap.values()) {
                        if (fc != null) {
                            fc.close();
                        }
                    }
                    fcMap.clear();
                }
            } catch (IOException e) {
            }
        }
    }

    @Override
    protected void initChild() {

    }

    protected void initBuffer(Long index) {
        if (!buffers.containsKey(index)) {
            try {
                synchronized (this) {
                    if (gotohell) {
                        return;
                    }
                    if (!buffers.containsKey(index)) {
                        FileChannel channel = initFile(index);
                        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                        initChild(index, buffer);
                        buffers.put(index, buffer);
                        fcMap.put(index, channel);
                    }
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
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
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public void delete() {
        release();
        File f = baseFile;
        int i = 0;
        while (f.exists()) {
            f.delete();
            i++;
            f = new File(baseFile.getAbsolutePath() + "_" + i);
        }
    }
}