package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.nio.read.IntNIOReader;
import com.fr.swift.cube.nio.write.IntNIOWriter;

/**
 * @author anchore
 * @date 2018/1/5
 */
class Int2IntPairsExtMapIo extends BaseIntPairsExtMapIo<Integer> {
    Int2IntPairsExtMapIo(String id) {
        super(id);
    }

    @Override
    protected Integer getEndFlag() {
        return IOConstant.NULL_INT;
    }

    @Override
    public void initKeyWriter() {
        if (keyWriter == null) {
            keyWriter = new IntNIOWriter(keyFile);
        }
    }

    @Override
    public void initKeyReader() {
        if (keyReader == null) {
            keyReader = new IntNIOReader(keyFile);
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
