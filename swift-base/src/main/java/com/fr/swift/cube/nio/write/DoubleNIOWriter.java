package com.fr.swift.cube.nio.write;

import com.fr.swift.cube.nio.NIOConstant;

import java.io.File;
import java.nio.DoubleBuffer;

public class DoubleNIOWriter extends AbstractNIOWriter<Double> {


    private DoubleBuffer doubleBuffer;

    public DoubleNIOWriter(File cacheFile) {
        super(cacheFile);
    }

    public DoubleNIOWriter(String path) {
        super(new File(path));
    }

    @Override
    public long getPageStep() {
        return NIOConstant.DOUBLE.PAGE_STEP;
    }

    @Override
    protected void initChild() {
        doubleBuffer = buffer.asDoubleBuffer();
    }

    @Override
    protected void releaseChild() {
        if (doubleBuffer != null) {
            doubleBuffer.clear();
            doubleBuffer = null;
        }
    }

    @Override
    protected void addValue(int row, Double value) {
        doubleBuffer.put(row, value == null ? Double.NaN : value.doubleValue());
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.DOUBLE.PAGE_MODE_TO_AND_WRITE_VALUE;
    }
}