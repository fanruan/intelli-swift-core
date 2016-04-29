package com.fr.bi.cal.stable.index.file.field;

import com.fr.bi.cal.stable.index.file.SingleCubeFile;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

public interface NIOCubeFile<K> extends SingleCubeFile {


    /**
     * 创建写的方法用完必须释放
     *
     * @return
     */
    public NIOWriter<K> createNIOWriter();


    /**
     * 释放 仅在创建写方法的同一个方法里面执行
     */
    public void clearWriter();

    /**
     * 创建读的方法，不需要释放
     *
     * @param manager
     * @return
     */
    public NIOReader<K> createNIOReader(SingleUserNIOReadManager manager);

}