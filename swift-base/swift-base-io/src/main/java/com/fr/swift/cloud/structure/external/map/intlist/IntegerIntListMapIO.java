package com.fr.swift.cloud.structure.external.map.intlist;

import com.fr.swift.cloud.cube.nio.read.IntNIOReader;
import com.fr.swift.cloud.cube.nio.write.IntNIOWriter;
import com.fr.swift.cloud.util.Util;

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
