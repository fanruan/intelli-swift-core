package com.fr.bi.cal.stable.index.file;


/**
 * Created by GUY on 2015/3/13.
 */
public class VersionFile extends AbstractOneValueICubeFile {

    public VersionFile(String path) {
        super(path);
    }

    public long write(int data) {
        String version = String.valueOf(data);
        return super.write(version);
    }

}