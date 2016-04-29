package com.fr.bi.cal.stable.index.file.field;

import com.fr.bi.stable.io.io.read.StringReadMappedList;
import com.fr.bi.stable.io.io.write.StringWriteMappedList;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;


/**
 * Created by GUY on 2015/3/12.
 */
public class CubeStringFile extends AbstractNIOCubeFile<String> {

    public CubeStringFile(String path) {
        super(path);
    }

    @Override
    public NIOWriter<String> createNIOWriter() {
        return createObject(StringWriteMappedList.class);
    }


    @Override
    public NIOReader<String> createNIOReader(SingleUserNIOReadManager manager) {
        checkManager(manager);
        return manager.getNIOReader(getPath(), StringReadMappedList.class);
    }
}