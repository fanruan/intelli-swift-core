package com.finebi.cube.data.disk.writer.primitive;

import com.finebi.cube.data.output.primitive.ICubeDoubleWriter;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.File;
import java.nio.DoubleBuffer;

public class BIDoubleNIOWriter extends BIBasicNIOWriter<Double> implements ICubeDoubleWriter {


    private DoubleBuffer doubleBuffer;

    public BIDoubleNIOWriter(File cacheFile) {
        super(cacheFile);
    }
    public BIDoubleNIOWriter(String path) {
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