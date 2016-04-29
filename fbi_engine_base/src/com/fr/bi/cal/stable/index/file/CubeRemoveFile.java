package com.fr.bi.cal.stable.index.file;


import com.fr.bi.cal.stable.index.file.field.AbstractNIOCubeFile;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.newio.read.LongNIOReader;
import com.fr.bi.stable.io.newio.write.LongNIOWriter;

/**
 * Created by GUY on 2015/3/18.
 */
public class CubeRemoveFile extends AbstractNIOCubeFile<Long> {
    public CubeRemoveFile(String path) {
        super(path);
    }

    /**
     * 创建写的方法用完必须释放
     *
     * @return
     */
    @Override
    public NIOWriter<Long> createNIOWriter() {
        return createObject(LongNIOWriter.class);
    }

    /**
     * 创建读的方法，不需要释放
     *
     * @param manager
     * @return
     */
    @Override
    public NIOReader<Long> createNIOReader(SingleUserNIOReadManager manager) {
        checkManager(manager);
        return manager.getNIOReader(getPath(), LongNIOReader.class);
    }
}