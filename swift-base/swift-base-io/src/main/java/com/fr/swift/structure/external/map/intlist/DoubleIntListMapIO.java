package com.fr.swift.structure.external.map.intlist;

import com.fr.swift.cube.nio.read.DoubleNIOReader;
import com.fr.swift.cube.nio.write.DoubleNIOWriter;
import com.fr.swift.util.Util;

import java.io.FileNotFoundException;

/**
 * @author wang
 * @date 2016/9/2
 */
class DoubleIntListMapIO extends BaseIntListExternalMapIO<Double> {
    public DoubleIntListMapIO(String ID_path) {
        super(ID_path);
    }

    @Override
    void initialKeyReader() throws FileNotFoundException {
        if (keyFile.exists()) {
            keyReader = new DoubleNIOReader(keyFile);
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    void initialKeyWriter() {
        keyWriter = new DoubleNIOWriter(keyFile);
    }

    @Override
    public boolean isEmpty(Double key) {
        return key == null || Util.equals(key, 0d);
    }
}
