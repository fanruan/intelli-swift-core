package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.nio.read.LongNIOReader;
import com.fr.swift.cube.nio.write.LongNIOWriter;

/**
 * @author anchore
 * @date 2018/1/5
 */
class Long2IntPairsExtMapIo extends BaseIntPairsExtMapIo<Long> {
    Long2IntPairsExtMapIo(String id) {
        super(id);
    }

    @Override
    protected Long getEndFlag() {
        return IOConstant.NULL_LONG;
    }

    @Override
    protected void initKeyWriter() {
        if (keyWriter == null) {
            keyWriter = new LongNIOWriter(keyFile);
        }
    }

    @Override
    protected void initKeyReader() {
        if (keyReader == null) {
            keyReader = new LongNIOReader(keyFile);
        }
    }

    @Override
    public void close() {
        super.close();
        if (keyWriter != null) {
            keyWriter.release();
            keyWriter = null;
        }
        if (keyReader != null) {
            keyReader.release();
            keyReader = null;
        }
    }
}
