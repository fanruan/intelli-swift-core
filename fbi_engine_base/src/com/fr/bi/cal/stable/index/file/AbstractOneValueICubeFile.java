package com.fr.bi.cal.stable.index.file;


import com.fr.bi.stable.io.io.ListWriter;
import com.fr.bi.stable.utils.file.BIFileUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOneValueICubeFile extends AbstractCubeFile
        implements ICubeFile<String> {

    protected AbstractOneValueICubeFile(String path) {
        super(path);
    }


    protected List<String> readAll() {
        return BIFileUtils.readFileByLines(createFile());
    }


    @Override
    public long write(String data) {
        List<String> v = new ArrayList<String>();
        v.add(data);
        ListWriter.writeValueListToFile(v, createFile());
        return v.size();
    }

    @Override
    public String read() {
        List<String> values = readAll();
        if (!values.isEmpty()) {
            return values.get(0);
        }
        return "0";
    }

}