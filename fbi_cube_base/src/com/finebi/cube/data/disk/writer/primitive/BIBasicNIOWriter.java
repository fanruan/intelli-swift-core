package com.finebi.cube.data.disk.writer.primitive;

import com.finebi.cube.data.ICubeSourceReleaseManager;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.mem.BIMemoryUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public abstract class BIBasicNIOWriter<T> implements ICubePrimitiveWriter<T> {


    protected MappedByteBuffer buffer;
    private File baseFile;
    private FileChannel fc;
    private long currentIndex = -1L;
    private ICubeSourceReleaseManager releaseManager;
    private long file_index = -1L;
    private boolean isReleased = false;

    public BIBasicNIOWriter(File cacheFile) {
        this.baseFile = cacheFile;
        if (!baseFile.exists()) {
            try {
                baseFile.getParentFile().mkdirs();
                baseFile.createNewFile();
            } catch (IOException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    protected abstract long getPageStep();

    protected abstract void releaseChild();

    protected abstract void initChild();


    @Override
    public void clear() {
        synchronized (this) {
            if (useReleaseManager()) {
                releaseManager.release(this);
            } else {
                releaseSource();
            }
        }
    }

    public void releaseSource() {
        if (!isReleased) {
            clearBuffer();
            currentIndex = -1L;
            file_index = -1L;
            isReleased = true;
        }
    }

    @Override
    public void forceRelease() {
        releaseSource();
    }

    @Override
    public boolean isForceReleased() {
        return isReleased;
    }

    private boolean useReleaseManager() {
        return releaseManager != null;
    }

    public void clearBuffer() {
        if (buffer != null) {
            releaseChild();
            BIMemoryUtils.un_map(buffer);
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
    public void recordSpecificPositionValue(long position, T value) {
        if (position >= 0) {
            initBuffer(position);
            addValue((int) getRow(position), value);
        } else {
            throw BINonValueUtils.illegalArgument("The value of position is" + position + ",which must be positive value.");
        }
    }

    public void setReleaseManager(ICubeSourceReleaseManager releaseManager) {
        this.releaseManager = releaseManager;
    }

    @Override
    public void flush() {
        buffer.force();
    }

    protected void initBuffer(long row) {
        long index = getIndex(row);
        if (index != currentIndex || buffer == null) {
            long fileIndex = row >> (NIOConstant.MAX_SINGLE_FILE_PART_SIZE + getPageStep());
            initFile(fileIndex);
            try {
                if (buffer != null) {
                    releaseChild();
                    BIMemoryUtils.un_map(buffer);
                }
                long start = getStart(index) - (fileIndex << NIOConstant.MAX_SINGLE_FILE_PART_MOVE_ALL);
                buffer = fc.map(FileChannel.MapMode.READ_WRITE, start, getLen(index));
                initChild();
                currentIndex = index;
            } catch (IOException e) {
                BILogger.getLogger().error(e.getMessage(), e);
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
                BILogger.getLogger().error(e.getMessage(), e);
            } catch (IOException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
            file_index = fileIndex;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIBasicNIOWriter)) return false;

        BIBasicNIOWriter<?> that = (BIBasicNIOWriter<?>) o;

        return !(baseFile != null ? !baseFile.equals(that.baseFile) : that.baseFile != null);

    }

    @Override
    public int hashCode() {
        return baseFile != null ? baseFile.hashCode() : 0;
    }
}