package com.fr.bi.cal.stable.index.file.field;

import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.newio.read.LongNIOReader;
import com.fr.bi.stable.io.newio.write.LongNIOWriter;

/**
 * Created by GUY on 2015/3/12.
 */
public class CubeLongFile extends AbstractNIOCubeFile<Long> {

    public CubeLongFile(String path) {
        super(path);
    }

    @Override
    public NIOWriter<Long> createNIOWriter() {
        return createObject(LongNIOWriter.class);
    }

    @Override
    public NIOReader<Long> createNIOReader(SingleUserNIOReadManager manager) {
        checkManager(manager);
        return manager.getNIOReader(getPath(), LongNIOReader.class);
    }


}