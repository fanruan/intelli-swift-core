package com.fr.swift.structure.external.map.intlist;

import com.fr.swift.cube.nio.read.IntNIOReader;
import com.fr.swift.cube.nio.write.IntNIOWriter;
import com.fr.swift.util.Util;

import java.io.FileNotFoundException;

/**
 * Created by wang on 2016/9/2.
 */
class IntegerIntListMapIO extends BaseIntListExternalMapIO<Integer> {
    public IntegerIntListMapIO(String ID_path) {
        super(ID_path);
    }

    @Override
    void initialKeyReader() throws FileNotFoundException {
        if (keyFile.exists()) {
            keyReader = new IntNIOReader(keyFile);
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    void initialKeyWriter() {
        keyWriter = new IntNIOWriter(keyFile);
    }

    @Override
    public boolean isEmpty(Integer key) {
        return key == null || Util.equals(key, 0);
    }
}
