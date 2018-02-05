package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Readers;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Writers;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.output.LongWriter;

/**
 * @author anchore
 * @date 2018/1/5
 */
class Long2IntPairsExtMapIo extends BaseIntPairsExtMapIo<Long> {
    private LongWriter keyWriter;
    private LongReader keyReader;

    Long2IntPairsExtMapIo(String id) {
        super(id);
    }

    @Override
    void writeKey(int pos, Long key) {
        initKeyWriter();
        keyWriter.put(pos, key);
    }

    @Override
    Long readKey(int pos) {
        initKeyReader();
        return keyReader.get(pos);
    }

    private void initKeyWriter() {
        if (keyWriter == null) {
            keyWriter = (LongWriter) Writers.build(keyLocation, new BuildConf(IoType.WRITE, DataType.LONG));
        }
    }

    private void initKeyReader() {
        if (keyReader == null) {
            keyReader = (LongReader) Readers.build(keyLocation, new BuildConf(IoType.READ, DataType.LONG));
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
