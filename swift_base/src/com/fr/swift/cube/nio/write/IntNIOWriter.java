package com.fr.swift.cube.nio.write;

import com.fr.swift.cube.nio.NIOConstant;

import java.io.File;
import java.nio.IntBuffer;

public class IntNIOWriter extends AbstractNIOWriter<Integer> {
    private IntBuffer intBuffer;

    public IntNIOWriter(File cacheFile) {
        super(cacheFile);
    }

    public IntNIOWriter(String path) {
        super(new File(path));
    }

    @Override
    public long getPageStep() {
        return NIOConstant.INTEGER.PAGE_STEP;
    }

    @Override
    protected void initChild() {
        intBuffer = buffer.asIntBuffer();
    }

    @Override
    protected void releaseChild() {
        if (intBuffer != null) {
            intBuffer.clear();
            intBuffer = null;
        }
    }

    @Override
    protected void addValue(int row, Integer value) {
        intBuffer.put(row, value);
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.INTEGER.PAGE_MODE_TO_AND_WRITE_VALUE;
    }
}