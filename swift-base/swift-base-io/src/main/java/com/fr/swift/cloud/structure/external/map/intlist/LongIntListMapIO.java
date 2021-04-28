package com.fr.swift.cloud.structure.external.map.intlist;

import com.fr.swift.cloud.cube.nio.read.LongNIOReader;
import com.fr.swift.cloud.cube.nio.write.LongNIOWriter;
import com.fr.swift.cloud.util.Util;

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
