package com.finebi.cube.engine.map;


import com.fr.bi.stable.io.newio.read.DoubleNIOReader;
import com.fr.bi.stable.io.newio.write.DoubleNIOWriter;
import com.fr.general.ComparatorUtils;

import java.io.FileNotFoundException;

/**
 * Created by FineSoft on 2015/7/15.
 */
public class DoubleIntListExternalMapIO extends ExternalMapIOIntList<Double> {


    public DoubleIntListExternalMapIO(String ID_Path) {
        super(ID_Path);


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