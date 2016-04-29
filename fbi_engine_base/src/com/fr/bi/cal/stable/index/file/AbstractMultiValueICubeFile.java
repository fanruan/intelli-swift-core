package com.fr.bi.cal.stable.index.file;

import com.fr.bi.stable.io.io.ListWriter;
import com.fr.bi.stable.utils.file.BIFileUtils;

import java.util.List;

/**
 * Created by GUY on 2015/3/12.
 */
public abstract class AbstractMultiValueICubeFile extends AbstractCubeFile implements ICubeFile<List<String>> {


    protected AbstractMultiValueICubeFile(String path) {
        super(path);
    }

    @Override
    public List<String> read() {
        return BIFileUtils.readFileByLines(createFile());
    }

    @Override
    public long write(List<String> data) {
        ListWriter.writeValueListToFile(data, createFile());
        return data.size();
    }
}