package com.fr.bi.cal.stable.index.file;


/**
 * Created by GUY on 2015/3/13.
 */
public class GroupLengthFile extends AbstractOneValueICubeFile {

    public GroupLengthFile(String path) {
        super(path);
    }

    public long write(long data) {
        String version = String.valueOf(data);
        return super.write(version);
    }

}