package com.finebi.cube.engine.map;


import com.fr.bi.stable.io.io.read.StringReadMappedList;
import com.fr.bi.stable.io.io.write.StringWriteMappedList;
import com.fr.general.ComparatorUtils;

import java.io.FileNotFoundException;

/**
 * Created by FineSoft on 2015/7/15.
 */
public class StringIntListExternalMapIO extends ExternalMapIOIntList<String> {


    public StringIntListExternalMapIO(String ID_Path) {
        super(ID_Path);
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
        return key == null || ComparatorUtils.equals(key, "");
    }
}