package com.finebi.cube.data.disk.writer.primitive;

import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.File;
import java.nio.IntBuffer;

public class BIIntegerNIOWriter extends BIBasicNIOWriter<Integer> implements ICubeIntegerWriter {
    private IntBuffer intBuffer;

    public BIIntegerNIOWriter(File cacheFile) {
        super(cacheFile);
    }

    public BIIntegerNIOWriter(String path) {
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
        intBuffer.put(row, value == null ? Integer.MIN_VALUE : value.intValue());
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.INTEGER.PAGE_MODE_TO_AND_WRITE_VALUE;
    }
}