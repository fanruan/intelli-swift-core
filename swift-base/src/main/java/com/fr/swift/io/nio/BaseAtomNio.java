package com.fr.swift.io.nio;

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
    static final int PAGE_SIZE = 22;

    private FileChannel ch;

    ByteBuffer buf;

    private boolean mapped = false;

    private final int pageSize;

    private int currentPage = -1;

    BaseAtomNio(String basePath) {
        this(basePath, PAGE_SIZE);
    }

    BaseAtomNio(String basePath, int pageSize) {
        super(basePath);
        this.pageSize = pageSize;
        init();
    }

    private void init() {
        if (write) {
            new File(basePath).mkdirs();
        }
    }

    int getPage(long pos) {
        return (int) (pos >> (pageSize - getStep()));
    }

    int getOffset(long pos) {
        return (int) ((pos << getStep()) & ((1 << pageSize) - 1));
    }

    void initBuf(int page) {
        if (currentPage == page) {
            return;
        }
        loadBuffer(page);
    }

    private void loadBuffer(int page) {
        releaseBuffer();

        try {
            RandomAccessFile file = new RandomAccessFile(String.format("%s/%d", basePath, page), write ? "rw" : "r");
            ch = file.getChannel();

            if (mapped) {
                buf = ch.map(MapMode.READ_WRITE, 0, 1 << pageSize);
            } else {
                if (buf == null) {
                    buf = ByteBuffer.allocate(1 << pageSize);
                }
                ch.read(buf);
                if (write) {
                    ch.position(0);
                }
            }

            currentPage = page;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseBuffer() {
        if (buf == null) {
            return;
        }
        if (mapped) {
            IoUtil.release((MappedByteBuffer) buf);
            buf = null;
            return;
        }
        if (write) {
            buf.flip();
            try {
                ch.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtil.close(ch);
            }
        }
        buf.clear();
    }

    void setBufPosition(int offset) {
        int newPos = offset + (1 << getStep());
        if (newPos > buf.position()) {
            buf.position(newPos);
        }
    }

    abstract int getStep();

    @Override
    public void flush() {
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public void release() {
        releaseBuffer();
    }
}