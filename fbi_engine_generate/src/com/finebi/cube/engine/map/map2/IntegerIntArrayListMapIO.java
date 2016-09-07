package com.finebi.cube.engine.map.map2;

import com.fr.bi.stable.io.newio.read.IntNIOReader;
import com.fr.bi.stable.io.newio.write.IntNIOWriter;
import com.fr.general.ComparatorUtils;

import java.io.FileNotFoundException;

/**
 * Created by wang on 2016/9/2.
 */
public class IntegerIntArrayListMapIO extends ExternalMapIOIntArrayList<Integer> {
    public IntegerIntArrayListMapIO(String ID_path) {
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
        return key == null || ComparatorUtils.equals(key, Integer.valueOf(0));
    }
}
