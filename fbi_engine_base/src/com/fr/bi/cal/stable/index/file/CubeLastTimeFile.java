package com.fr.bi.cal.stable.index.file;


/**
 * Created by GUY on 2015/3/18.
 */
public class CubeLastTimeFile extends AbstractOneValueICubeFile {

    public CubeLastTimeFile(String path) {
        super(path);
    }


    public void write() {
        long t = System.currentTimeMillis();
        super.write(String.valueOf(t));
    }
}