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

    private int curBuf = -1, curFile = -1;

    private int curBufStart = -1;

    BaseAtomNio(NioConf conf) {
        super(conf);
        init();
    }

    private void init() {
        if (conf.isWrite()) {
            new File(conf.getPath()).mkdirs();
        }
    }

    int nthBuf(long pos) {
        return (int) (pos >> (conf.getBufSize() - getStep()));
    }

    int bufOffset(long pos) {
        return (int) ((pos << getStep()) & ((1 << conf.getBufSize()) - 1));
    }

    void initBuf(int nthBuf) {
        if (curBuf != nthBuf) {
            releaseBuf(false);
            loadBuffer(nthBuf);
        }
    }

    private void loadBuffer(int nthBuf) {
        int nthFile = nthFile(nthBuf);
        try {
            if (curFile != nthFile) {
                IoUtil.close(ch);
                ch = new RandomAccessFile(String.format("%s/%d", conf.getPath(), nthFile), conf.isRead() ? "r" : "rw").getChannel();
                curFile = nthFile;
            }

            int fileOffset = fileOffset(nthBuf);
            if (conf.isMapped()) {
                buf = ch.map(conf.isRead() ? MapMode.READ_ONLY : MapMode.READ_WRITE, fileOffset, 1 << conf.getBufSize());
            } else {
                if (buf == null) {
                    buf = ByteBuffer.allocateDirect(1 << conf.getBufSize());
                }
                if (conf.isAppend()) {
                    curBufStart = ch.size() < fileOffset ? 0 : ((int) (ch.size() - fileOffset));
                } else {
                    curBufStart = 0;
                    ch.read(buf, fileOffset);
                }
            }

            curBuf = nthBuf;
        } catch (IOException e) {
            IoUtil.release(buf);
            IoUtil.close(ch);
            SwiftLoggers.getLogger().error(e);
        }
    }

    private int nthFile(int nthBuf) {
        return nthBuf >> (conf.getFileSize() - conf.getBufSize());
    }

    private int fileOffset(int nthBuf) {
        return (nthBuf & ((1 << (conf.getFileSize() - conf.getBufSize())) - 1)) << conf.getBufSize();
    }

    private void releaseBuf(boolean finalRelease) {
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
                buf.position(curBufStart);
                try {
                    ch.write(buf, fileOffset(curBuf) + curBufStart);
                } catch (IOException e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }

        buf.clear();
        curBuf = -1;
        curFile = -1;
        curBufStart = -1;
        if (finalRelease) {
            IoUtil.release(buf);
            buf = null;
            IoUtil.close(ch);
        }
    }

    void setBufPos(int offset) {
        int newPos = offset + (1 << getStep());
        if (newPos > buf.position()) {
            buf.position(newPos);
        }

        if (curBufStart > offset) {
            curBufStart = offset < 0 ? 0 : offset;
        }
    }

    abstract int getStep();

    @Override
    public void release() {
        releaseBuf(true);
    }
}