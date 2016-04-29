package com.finebi.cube.data.disk.writer.primitive;

import com.finebi.cube.data.output.primitive.ICubeByteWriter;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.File;

public class BIByteNIOWriter extends BIBasicNIOWriter<Byte> implements ICubeByteWriter {

    public BIByteNIOWriter(File cacheFile) {
        super(cacheFile);
    }

    public BIByteNIOWriter(String path) {
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