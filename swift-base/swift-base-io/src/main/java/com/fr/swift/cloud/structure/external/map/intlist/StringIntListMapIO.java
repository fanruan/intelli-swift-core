package com.fr.swift.cloud.structure.external.map.intlist;

import com.fr.swift.cloud.cube.nio.read.StringReadMappedList;
import com.fr.swift.cloud.cube.nio.write.StringWriteMappedList;
import com.fr.swift.cloud.util.Strings;

import java.io.FileNotFoundException;

/**
 * Created by wang on 2016/9/2.
 */
class StringIntListMapIO extends BaseIntListExternalMapIO<String> {
    public StringIntListMapIO(String ID_path) {
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
        return Strings.isEmpty(key);
    }
}
