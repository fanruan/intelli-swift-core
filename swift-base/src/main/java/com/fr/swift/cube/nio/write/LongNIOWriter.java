package com.fr.swift.cube.nio.write;

import com.fr.swift.cube.nio.NIOConstant;

import java.io.File;
import java.nio.LongBuffer;

public class LongNIOWriter extends AbstractNIOWriter<Long> {
    private LongBuffer longBuffer;

    public LongNIOWriter(File cacheFile) {
        super(cacheFile);
    }

    public LongNIOWriter(String path) {
        super(new File(path));
    }

    @Override
    public long getPageStep() {
        return NIOConstant.LONG.PAGE_STEP;
    }

    @Override
    protected void initChild() {
        longBuffer = buffer.asLongBuffer();
    }

    @Override
    protected void releaseChild() {
        if (longBuffer != null) {
            longBuffer.clear();
            longBuffer = null;
        }
    }

    @Override
    protected void addValue(int row, Long value) {
        longBuffer.put(row, value == null ? NIOConstant.LONG.NULL_VALUE : value);
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.LONG.PAGE_MODE_TO_AND_WRITE_VALUE;
    }
}