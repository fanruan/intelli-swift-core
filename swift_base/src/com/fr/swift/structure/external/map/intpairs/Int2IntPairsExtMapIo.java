package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Readers;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Writers;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.output.IntWriter;

/**
 * @author anchore
 * @date 2018/1/5
 */
class Int2IntPairsExtMapIo extends BaseIntPairsExtMapIo<Integer> {
    private IntWriter keyWriter;
    private IntReader keyReader;

    Int2IntPairsExtMapIo(String id) {
        super(id);
    }

    @Override
    void writeKey(int pos, Integer key) {
        initKeyWriter();
        keyWriter.put(pos, key);
    }

    @Override
    Integer readKey(int pos) {
        initKeyReader();
        return keyReader.get(pos);
    }

    private void initKeyWriter() {
        if (keyWriter == null) {
            keyWriter = (IntWriter) Writers.build(keyLocation, new BuildConf(IoType.WRITE, DataType.INT));
        }
    }

    private void initKeyReader() {
        if (keyReader == null) {
            keyReader = (IntReader) Readers.build(keyLocation, new BuildConf(IoType.READ, DataType.INT));
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
