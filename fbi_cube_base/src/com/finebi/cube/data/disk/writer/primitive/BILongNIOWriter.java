package com.finebi.cube.data.disk.writer.primitive;

import com.finebi.cube.data.output.primitive.ICubeLongWriter;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.File;
import java.nio.LongBuffer;

public class BILongNIOWriter extends BIBasicNIOWriter<Long> implements ICubeLongWriter {
    private LongBuffer longBuffer;

    public BILongNIOWriter(File cacheFile) {
        super(cacheFile);
    }

    public BILongNIOWriter(String path) {
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
        longBuffer.put(row, value == null ? NIOConstant.LONG.NULL_VALUE : value.longValue());
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.LONG.PAGE_MODE_TO_AND_WRITE_VALUE;
    }
}