package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.IoUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * @author anchore
 * @date 2018/7/20
 */
abstract class BaseAtomNio extends BaseNio {

    ByteBuffer buf;

    private FileChannel ch;

    private int currentBuf = -1, currentFile = -1;

    private int currentStart = -1;

    BaseAtomNio(NioConf conf) {
        super(conf);
        init();
    }

    private void init() {
        if (conf.isWrite()) {
            new File(conf.getPath()).mkdirs();
        }
    }

    int getPage(long pos) {
        return (int) (pos >> (conf.getBufSize() - getStep()));
    }

    int getOffset(long pos) {
        return (int) ((pos << getStep()) & ((1 << conf.getBufSize()) - 1));
    }

    void initBuf(int nthBuf) {
        if (currentBuf == nthBuf) {
            return;
        }
        releaseBuffer(false);
        loadBuffer(nthBuf);
    }

    private void loadBuffer(int nthBuf) {
        int file = getFile(nthBuf);
        try {
            if (currentFile != file) {
                IoUtil.close(ch);
                ch = new RandomAccessFile(String.format("%s/%d", conf.getPath(), file), conf.isRead() ? "r" : "rw").getChannel();
                currentFile = file;
            }

            int fileOffset = getFileOffset(nthBuf);
            if (conf.isMapped()) {
                buf = ch.map(conf.isRead() ? MapMode.READ_ONLY : MapMode.READ_WRITE, fileOffset, 1 << conf.getBufSize());
            } else {
                if (buf == null) {
                    buf = ByteBuffer.allocateDirect(1 << conf.getBufSize());
                }
                if (conf.isAppend()) {
                    currentStart = ch.size() < fileOffset ? 0 : ((int) (ch.size() - fileOffset));
                } else {
                    currentStart = 0;
                    ch.read(buf, fileOffset);
                }
            }

            currentBuf = nthBuf;
        } catch (IOException e) {
            IoUtil.release(buf);
            IoUtil.close(ch);
            SwiftLoggers.getLogger().error(e);
        }
    }

    private int getFile(int nthBuf) {
        return nthBuf >> (conf.getFileSize() - conf.getBufSize());
    }

    private int getFileOffset(int nthBuf) {
        return (nthBuf & ((1 << (conf.getFileSize() - conf.getBufSize())) - 1)) << conf.getBufSize();
    }

    private void releaseBuffer(boolean finalRelease) {
        if (buf == null) {
            return;
        }

        if (conf.isWrite()) {
            if (conf.isMapped()) {
                // mapped写
                ((MappedByteBuffer) buf).force();
            } else {
                // 非mapped写，buf视为内存块
                buf.limit(buf.position());
                buf.position(currentStart);
                try {
                    ch.write(buf, currentStart + getFileOffset(currentBuf));
                } catch (IOException e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }

        buf.clear();
        currentBuf = -1;
        currentFile = -1;
        currentStart = -1;
        if (finalRelease) {
            IoUtil.release(buf);
            buf = null;
            IoUtil.close(ch);
        }
    }

    void setBufPosition(int offset) {
        int newPos = offset + (1 << getStep());
        if (newPos > buf.position()) {
            buf.position(newPos);
        }

        if (currentStart > offset) {
            currentStart = offset < 0 ? 0 : offset;
        }
    }

    abstract int getStep();

    @Override
    public void flush() {
    }

    @Override
    public void release() {
        releaseBuffer(true);
    }
}