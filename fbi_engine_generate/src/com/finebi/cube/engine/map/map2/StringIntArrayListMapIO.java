package com.finebi.cube.engine.map.map2;

import com.fr.bi.stable.io.io.read.StringReadMappedList;
import com.fr.bi.stable.io.io.write.StringWriteMappedList;
import com.fr.bi.stable.io.newio.read.IntNIOReader;
import com.fr.bi.stable.io.newio.write.IntNIOWriter;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.third.org.apache.poi.util.StringUtil;

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
