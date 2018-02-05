package com.fr.swift.structure.external.map.intlist.map2;

import com.fr.general.ComparatorUtils;
import com.fr.swift.cube.nio.read.LongNIOReader;
import com.fr.swift.cube.nio.write.LongNIOWriter;

import java.io.FileNotFoundException;

/**
 * Created by wang on 2016/9/2.
 */
public class LongIntArrayListMapIO extends ExternalMapIOIntArrayList<Long> {
    public LongIntArrayListMapIO(String ID_path) {
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
        return key == null || ComparatorUtils.equals(key, Long.valueOf(0));
    }
}
