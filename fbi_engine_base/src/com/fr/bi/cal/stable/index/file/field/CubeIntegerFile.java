package com.fr.bi.cal.stable.index.file.field;

import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.newio.read.IntNIOReader;
import com.fr.bi.stable.io.newio.write.IntNIOWriter;

/**
 * Created by GUY on 2015/3/12.
 */
public class CubeIntegerFile extends AbstractNIOCubeFile<Integer> {

    public CubeIntegerFile(String path) {
        super(path);
    }


    @Override
    public NIOWriter<Integer> createNIOWriter() {
        return createObject(IntNIOWriter.class);
    }

    @Override
    public NIOReader<Integer> createNIOReader(SingleUserNIOReadManager manager) {
        checkManager(manager);
        return manager.getNIOReader(getPath(), IntNIOReader.class);
    }


}