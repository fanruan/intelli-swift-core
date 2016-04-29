package com.finebi.cube.engine.map;


import com.fr.bi.stable.io.newio.read.LongNIOReader;
import com.fr.bi.stable.io.newio.write.LongNIOWriter;
import com.fr.general.ComparatorUtils;

import java.io.FileNotFoundException;

/**
 * Created by FineSoft on 2015/7/16.
 */
public class LongIntListExternalMapIO extends ExternalMapIOIntList<Long> {
    public LongIntListExternalMapIO(String ID_Path) {
        super(ID_Path);
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