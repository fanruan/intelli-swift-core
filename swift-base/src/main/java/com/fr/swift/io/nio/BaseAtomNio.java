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
    private FileChannel ch;

    ByteBuffer buf;

    private int currentPage = -1;

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
        releaseBuffer();
        loadBuffer(page);
    }

    private void loadBuffer(int page) {
        try {
            RandomAccessFile file = new RandomAccessFile(String.format("%s/%d", conf.getPath(), page), conf.isRead() ? "r" : "rw");
            ch = file.getChannel();

            if (conf.isMapped()) {
                buf = ch.map(conf.isRead() ? MapMode.READ_ONLY : MapMode.READ_WRITE, 0, 1 << conf.getPageSize());
            } else {
                if (buf == null) {
                    buf = ByteBuffer.allocate(1 << conf.getPageSize());
                }
                if (conf.isAppend()) {
                    currentStart = (int) ch.size();
                } else {
                    currentStart = 0;
                    ch.read(buf);
                }
            }

            currentPage = page;
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private void releaseBuffer() {
        if (buf == null) {
            return;
        }
        if (conf.isMapped()) {
            IoUtil.release((MappedByteBuffer) buf);
            buf = null;
            return;
        }
        if (conf.isWrite()) {
            buf.limit(buf.position());
            buf.position(currentStart);
            try {
                ch.write(buf, currentStart);
            } catch (IOException e) {
                SwiftLoggers.getLogger().error(e);
            } finally {
                IoUtil.close(ch);
            }
        } else {
            IoUtil.close(ch);
        }

        buf.clear();
        currentPage = -1;
        currentStart = -1;
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
        File f = new File(conf.getPath());
        return f.exists() && f.list() != null;
    }

    @Override
    public void release() {
        releaseBuffer();
    }
}