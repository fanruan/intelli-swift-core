package com.fr.swift.cube.nio.write;

import com.fr.swift.cube.nio.MemoryUtils;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.cube.nio.NIOReadWriter;
import com.fr.swift.cube.nio.NIOWriter;
import com.fr.swift.log.SwiftLoggers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public abstract class AbstractNIOWriter<T> extends NIOReadWriter implements NIOWriter<T> {


    protected MappedByteBuffer buffer;
    private File baseFile;
    private FileChannel fc;
    private long currentIndex = -1L;

    private long file_index = -1L;

    public AbstractNIOWriter(File cacheFile) {
        this.baseFile = cacheFile;
        if (!baseFile.exists()) {
            try {
                baseFile.getParentFile().mkdirs();
                baseFile.createNewFile();
            } catch (IOException e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void release() {
        synchronized (this) {
            clearBuffer();
            currentIndex = -1L;
            file_index = -1L;
        }
    }

    public void clearBuffer() {
        if (buffer != null) {
            releaseChild();
            MemoryUtils.un_map(buffer);
            buffer = null;
        }
        try {
            if (fc != null) {
                fc.close();
                fc = null;
            }
        } catch (IOException ignore) {
        }
    }

    protected abstract long getPageModeValue();

    protected abstract void addValue(int row, T value);

    @Override
    public void add(long row, T value) {
        initBuffer(row);
        addValue((int) getRow(row), value);
    }

    protected void initBuffer(long row) {
        long index = getIndex(row);
        if (index != currentIndex || buffer == null) {
            long fileIndex = row >> (NIOConstant.MAX_SINGLE_FILE_PART_SIZE + getPageStep());
            initFile(fileIndex);
            try {
                if (buffer != null) {
                    releaseChild();
                    MemoryUtils.un_map(buffer);
                }
                long start = getStart(index) - (fileIndex << NIOConstant.MAX_SINGLE_FILE_PART_MOVE_ALL);
                buffer = fc.map(FileChannel.MapMode.READ_WRITE, start, getLen(index));
                initChild();
                currentIndex = index;
            } catch (IOException e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
    }

    private long getRow(long row) {
        if (currentIndex > NIOConstant.MINFILE_SIZE) {
            return row & getPageModeValue();
        } else if (currentIndex == 0) {
            return row;
        } else {
            return row & ((1L << (getPageStep() + currentIndex - NIOConstant.MINFILE_SIZE - 1)) - 1);
        }
    }

    private long getIndex(long row) {
        long index = row >> getPageStep();
        if (index != 0) {
            return index + NIOConstant.MINFILE_SIZE;
        }

        long temp = row >> (getPageStep() - NIOConstant.MINFILE_SIZE);
        long mindex = 0;
        while (temp > 0) {
            temp = temp >> 1;
            mindex++;
        }
        return mindex;
    }

    private long getStart(long index) {
        if (index > NIOConstant.MINFILE_SIZE) {
            return (index - NIOConstant.MINFILE_SIZE) << NIOConstant.PAGE_STEP;
        } else if (index == 0) {
            return 0;
        } else {
            return 1L << (NIOConstant.PAGE_STEP + index - 1 - NIOConstant.MINFILE_SIZE);
        }
    }

    private long getLen(long index) {
        if (index > NIOConstant.MINFILE_SIZE) {
            return NIOConstant.PAGE_SIZE;
        } else if (index == 0) {
            return 1024;
        } else {
            return 1L << (NIOConstant.PAGE_STEP + index - 1 - NIOConstant.MINFILE_SIZE);
        }
    }

    private void initFile(long fileIndex) {
        if (fileIndex != file_index) {
            //兼容之前的
            File cacheFile = null;
            if (fileIndex == 0) {
                cacheFile = baseFile;
            } else {
                cacheFile = new File(baseFile.getAbsolutePath() + "_" + fileIndex);
            }
            clearBuffer();
            try {
                if (cacheFile.exists()) {
                    cacheFile.delete();
                }
                cacheFile.createNewFile();
                fc = new RandomAccessFile(cacheFile, "rw").getChannel();
            } catch (FileNotFoundException e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            } catch (IOException e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
            file_index = fileIndex;
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void setPos(long pos) {

    }
}