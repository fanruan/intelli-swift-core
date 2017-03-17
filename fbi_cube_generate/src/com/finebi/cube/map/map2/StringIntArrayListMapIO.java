package com.finebi.cube.map.map2;

import com.fr.bi.stable.io.io.read.StringReadMappedList;
import com.fr.bi.stable.io.io.write.StringWriteMappedList;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;

import java.io.FileNotFoundException;

/**
 * Created by wang on 2016/9/2.
 */
public class StringIntArrayListMapIO extends ExternalMapIOIntArrayList<String> {
    public StringIntArrayListMapIO(String ID_path) {
        super(ID_path);
    }

    @Override
    void initialKeyReader() throws FileNotFoundException {
        if (keyFile.exists()) {
            keyReader = new StringReadMappedList(keyFile);
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    void initialKeyWriter() {
        keyWriter = new StringWriteMappedList(keyFile.getAbsolutePath());
    }

    @Override
    public boolean isEmpty(String key) {
        return key == null || ComparatorUtils.equals(key, StringUtils.EMPTY);
    }
}
