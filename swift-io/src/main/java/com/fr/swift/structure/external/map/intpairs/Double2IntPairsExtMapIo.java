package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.nio.read.DoubleNIOReader;
import com.fr.swift.cube.nio.write.DoubleNIOWriter;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2018/1/5
 */
class Double2IntPairsExtMapIo extends BaseIntPairsExtMapIo<Double> {
    Double2IntPairsExtMapIo(String id) {
        super(id);
    }

    @Override
    protected Double getEndFlag() {
        return IOConstant.NULL_DOUBLE;
    }

    @Override
    protected void initKeyWriter() {
        if (keyWriter == null) {
            keyWriter = new DoubleNIOWriter(keyFile);
        }
    }

    @Override
    protected void initKeyReader() {
        if (keyReader == null) {
            keyReader = new DoubleNIOReader(keyFile);
        }
    }

    @Override
    public void close() {
        try {
            super.close();
        } finally {
            IoUtil.release(keyWriter, keyReader);
            keyWriter = null;
            keyReader = null;
        }
    }
}
