package com.fr.bi.cal.stable.index.file.field.index.group.gzip;

import com.fr.bi.cal.stable.index.file.field.AbstractNIOCubeFile;
import com.fr.bi.stable.io.io.read.ByteReadMappedList;
import com.fr.bi.stable.io.io.write.ByteWriteMappedList;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

/**
 * Created by GUY on 2015/3/20.
 */
public class GroupValueIndexFile extends AbstractNIOCubeFile<byte[]> {

    public GroupValueIndexFile(String path) {
        super(path);
    }

    @Override
    public NIOWriter<byte[]> createNIOWriter() {
        return createObject(ByteWriteMappedList.class);
    }


    @Override
    public NIOReader<byte[]> createNIOReader(SingleUserNIOReadManager manager) {
        checkManager(manager);
        return manager.getNIOReader(getPath(), ByteReadMappedList.class);
    }
}