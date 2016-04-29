package com.finebi.cube.engine.map;


import com.fr.bi.stable.io.newio.read.IntNIOReader;
import com.fr.bi.stable.io.newio.write.IntNIOWriter;
import com.fr.general.ComparatorUtils;

import java.io.FileNotFoundException;

/**
 * Created by FineSoft on 2015/7/16.
 */
public class IntegerIntListExternalMapIO extends ExternalMapIOIntList<Integer> {
    public IntegerIntListExternalMapIO(String ID_Path) {
        super(ID_Path);
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