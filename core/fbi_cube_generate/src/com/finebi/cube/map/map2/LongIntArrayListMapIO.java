package com.finebi.cube.map.map2;

import com.fr.bi.stable.io.newio.read.LongNIOReader;
import com.fr.bi.stable.io.newio.write.LongNIOWriter;
import com.fr.general.ComparatorUtils;

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
