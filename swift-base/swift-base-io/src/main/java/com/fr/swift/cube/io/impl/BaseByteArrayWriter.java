package com.fr.swift.cube.io.impl;

import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.cube.io.output.ByteWriter;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.cube.io.output.LongWriter;
import com.fr.swift.lang.Nullable;
import com.fr.swift.util.IoUtil;

import java.io.OutputStream;

/**
 * @author anchore
 * @date 2019/3/25
 */
public class BaseByteArrayWriter implements ByteArrayWriter {

    private ByteWriter dataWriter;

    private LongWriter posWriter;

    private IntWriter lenWriter;

    private boolean isOverwrite;

    @Nullable("null if overwrite")
    private LongWriter lastPosWriter;

    @Nullable("null if overwrite")
    private LongReader lastPosReader;

    private long curPos = 0;

    public BaseByteArrayWriter(ByteWriter dataWriter, LongWriter posWriter, IntWriter lenWriter, boolean isOverwrite, @Nullable LongWriter lastPosWriter, @Nullable LongReader lastPosReader) {
        this.dataWriter = dataWriter;
        this.posWriter = posWriter;
        this.lenWriter = lenWriter;
        this.isOverwrite = isOverwrite;
        this.lastPosWriter = lastPosWriter;
        this.lastPosReader = lastPosReader;

        init();
    }

    private void init() {
        if (!isOverwrite && lastPosReader.isReadable()) {
            curPos = lastPosReader.get(0);
        }
    }

    @Override
    public void put(long pos, byte[] val) {
        int len = val == null ? 0 : val.length;
        posWriter.put(pos, curPos);
        lenWriter.put(pos, len);
        for (int i = 0; i < len; i++) {
            dataWriter.put(curPos + i, val[i]);
        }

        curPos += len;
    }

    @Override
    public OutputStream putStream(final long pos) {
        posWriter.put(pos, curPos);

        return new OutputStream() {
            long cursor = curPos;

            @Override
            public void write(int b) {
                dataWriter.put(cursor++, (byte) b);
            }

            @Override
            public void close() {
                try {
                    lenWriter.put(pos, (int) (cursor - curPos));
                } finally {
                    curPos = cursor;
                }
            }
        };
    }

    @Override
    public void resetContentPosition() {
        curPos = 0;
    }

    @Override
    public void release() {
        try {
            if (!isOverwrite) {
                lastPosWriter.put(0, curPos);
            }
        } finally {
            IoUtil.release(lastPosWriter, dataWriter, posWriter, lenWriter);
        }
    }
}