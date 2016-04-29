package com.fr.bi.cal.stable.index.file;


/**
 * 保存简单索引行数信息
 * Created by GUY on 2015/3/12.
 */
public class CubeOtherFile extends AbstractOneValueICubeFile {

    public CubeOtherFile(String path) {
        super(path);
    }

    public long write(long data) {
        String version = String.valueOf(data);
        return super.write(version);
    }

}