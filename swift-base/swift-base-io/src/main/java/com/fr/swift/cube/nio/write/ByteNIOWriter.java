package com.fr.swift.cube.nio.write;

import com.fr.swift.cube.nio.NIOConstant;

import java.io.File;

public class ByteNIOWriter extends AbstractNIOWriter<Byte> {

    public ByteNIOWriter(File cacheFile) {
        super(cacheFile);
    }

    public ByteNIOWriter(String path) {
        super(new File(path));
    }

    @Override
    public long getPageStep() {
        return NIOConstant.BYTE.PAGE_STEP;
    }

    @Override
    protected void initChild() {
    }

    @Override
    protected void releaseChild() {
    }

    @Override
    protected void addValue(int row, Byte value) {
        buffer.put(row, value);
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.BYTE.PAGE_MODE_TO_AND_WRITE_VALUE;
    }

}