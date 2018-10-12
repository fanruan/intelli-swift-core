package com.fr.swift.io.nio;

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

    private int currentPage = -1, currentFilePage = -1;

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
        return (int) (pos >> (conf.getPageSize() - getStep()));
    }

    int getOffset(long pos) {
        return (int) ((pos << getStep()) & ((1 << conf.getPageSize()) - 1));
    }

    void initBuf(int page) {
        if (currentPage == page) {
            return;
        }
        releaseBuffer(false);
        loadBuffer(page);
    }

    private void loadBuffer(int page) {
        int filePage = getFilePage(page);
        try {
            if (currentFilePage != filePage) {
                IoUtil.close(ch);
                ch = new RandomAccessFile(String.format("%s/%d", conf.getPath(), filePage), conf.isRead() ? "r" : "rw").getChannel();
                currentFilePage = filePage;
            }

            int fileOffset = getFileOffset(page);
            if (conf.isMapped()) {
                buf = ch.map(conf.isRead() ? MapMode.READ_ONLY : MapMode.READ_WRITE, fileOffset, 1 << conf.getPageSize());
            } else {
                if (buf == null) {
                    buf = ByteBuffer.allocateDirect(1 << conf.getPageSize());
                }
                if (conf.isAppend()) {
                    currentStart = (int) (ch.size() - fileOffset);
                } else {
                    currentStart = 0;
                    ch.read(buf, fileOffset);
                }
            }

            currentPage = page;
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private int getFilePage(int page) {
        return page >> (conf.getFileSize() - conf.getPageSize());
    }

    private int getFileOffset(int page) {
        return (page & ((1 << (conf.getFileSize() - conf.getPageSize())) - 1)) << conf.getPageSize();
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
                    ch.write(buf, currentStart + getFileOffset(currentPage));
                } catch (IOException e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }

        buf.clear();
        currentPage = -1;
        currentFilePage = -1;
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
            currentStart = offset;
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