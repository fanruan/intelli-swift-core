package com.fr.bi.cal.stable.index.file.field;

import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.newio.read.DoubleNIOReader;
import com.fr.bi.stable.io.newio.write.DoubleNIOWriter;

/**
 * Created by GUY on 2015/3/12.
 */
public class CubeDoubleFile extends AbstractNIOCubeFile<Double> {

    public CubeDoubleFile(String path) {
        super(path);
    }

    @Override
    public NIOWriter<Double> createNIOWriter() {
        return createObject(DoubleNIOWriter.class);
    }

    @Override
    public NIOReader<Double> createNIOReader(SingleUserNIOReadManager manager) {
        checkManager(manager);
        return manager.getNIOReader(getPath(), DoubleNIOReader.class);
    }


}