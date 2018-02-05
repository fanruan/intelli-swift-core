package com.fr.swift.structure.external.map.intlist.map2;

import com.fr.general.ComparatorUtils;
import com.fr.swift.cube.nio.read.DoubleNIOReader;
import com.fr.swift.cube.nio.write.DoubleNIOWriter;

import java.io.FileNotFoundException;

/**
 * Created by wang on 2016/9/2.
 */
public class DoubleIntArrayListMapIO extends ExternalMapIOIntArrayList<Double> {
    public DoubleIntArrayListMapIO(String ID_path) {
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
        return key == null || ComparatorUtils.equals(key, Double.valueOf(0));
    }
}
