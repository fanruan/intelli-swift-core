package com.fr.swift.structure.external.map.intlist;

import com.fr.swift.cube.nio.read.LongNIOReader;
import com.fr.swift.cube.nio.write.LongNIOWriter;
import com.fr.swift.util.Util;

import java.io.FileNotFoundException;

/**
 * Created by wang on 2016/9/2.
 */
class LongIntListMapIO extends BaseIntListExternalMapIO<Long> {
    public LongIntListMapIO(String ID_path) {
        super(ID_path);
    }

    @Override
    void initialKeyReader() throws FileNotFoundException {
        if (keyFile.exists()) {
            keyReader = new LongNIOReader(keyFile);
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    void initialKeyWriter() {
        keyWriter = new LongNIOWriter(keyFile);
    }

    @Override
    public boolean isEmpty(Long key) {
        return key == null || Util.equals(key, 0L);
    }
}
