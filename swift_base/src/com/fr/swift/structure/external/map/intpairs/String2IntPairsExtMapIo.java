package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Readers;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Writers;
import com.fr.swift.cube.io.input.StringReader;
import com.fr.swift.cube.io.output.StringWriter;

/**
 * @author anchore
 * @date 2018/1/5
 */
class String2IntPairsExtMapIo extends BaseIntPairsExtMapIo<String> {
    private StringWriter keyWriter;
    private StringReader keyReader;

    String2IntPairsExtMapIo(String id) {
        super(id);
    }

    @Override
    void writeKey(int pos, String key) {
        initKeyWriter();
        keyWriter.put(pos, key);
    }

    @Override
    String readKey(int pos) {
        initKeyReader();
        return keyReader.get(pos);
    }

    private void initKeyWriter() {
        if (keyWriter == null) {
            keyWriter = (StringWriter) Writers.build(keyLocation, new BuildConf(IoType.WRITE, DataType.STRING));
        }
    }

    private void initKeyReader() {
        if (keyReader == null) {
            keyReader = (StringReader) Readers.build(keyLocation, new BuildConf(IoType.READ, DataType.STRING));
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
