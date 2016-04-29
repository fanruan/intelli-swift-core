package com.fr.bi.cal.stable.index.file;


/**
 * Created by GUY on 2015/3/12.
 */
public interface ICubeFile<T> extends SingleCubeFile {

    /**
     * 从cube文件中读取数据
     *
     * @return 读取出来的数据
     */
     T read();

    /**
     * 向cube文件中写入数据
     * @param data 待写入的数据
     * @return 写入文件的行数
     */
     long write(T data);
}