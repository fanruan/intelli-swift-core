package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.nio.read.StringReadMappedList;
import com.fr.swift.cube.nio.write.StringWriteMappedList;
import com.fr.swift.structure.Pair;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/1/5
 */
class String2IntPairsExtMapIo extends BaseIntPairsExtMapIo<String> {
    String2IntPairsExtMapIo(String id) {
        super(id);
    }

    @Override
    protected String getEndCookie() {
        return IOConstant.NULL_STRING;
    }

    @Override
    protected void writeEndCookie() {
        write(IOConstant.NULL_STRING, Collections.<Pair<Integer, Integer>>emptyList());
    }

    @Override
    protected void initKeyWriter() {
        if (keyWriter == null) {
            keyWriter = new StringWriteMappedList(keyFile.getAbsolutePath());
        }
    }

    @Override
    protected void initKeyReader() {
        if (keyReader == null) {
            keyReader = new StringReadMappedList(keyFile.getAbsolutePath());
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
