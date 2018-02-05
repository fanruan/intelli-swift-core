package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Readers;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Writers;
import com.fr.swift.cube.io.input.DoubleReader;
import com.fr.swift.cube.io.output.DoubleWriter;

/**
 * @author anchore
 * @date 2018/1/5
 */
class Double2IntPairsExtMapIo extends BaseIntPairsExtMapIo<Double> {
    private DoubleWriter keyWriter;
    private DoubleReader keyReader;

    Double2IntPairsExtMapIo(String id) {
        super(id);
    }

    @Override
    void writeKey(int pos, Double key) {
        initKeyWriter();
        keyWriter.put(pos, key);
    }

    @Override
    Double readKey(int pos) {
        initKeyReader();
        return keyReader.get(pos);
    }

    private void initKeyWriter() {
        if (keyWriter == null) {
            keyWriter = (DoubleWriter) Writers.build(keyLocation, new BuildConf(IoType.WRITE, DataType.DOUBLE));
        }
    }

    private void initKeyReader() {
        if (keyReader == null) {
            keyReader = (DoubleReader) Readers.build(keyLocation, new BuildConf(IoType.READ, DataType.DOUBLE));
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
