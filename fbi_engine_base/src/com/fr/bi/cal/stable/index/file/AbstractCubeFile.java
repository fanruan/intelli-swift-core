package com.fr.bi.cal.stable.index.file;

import java.io.File;

public abstract class AbstractCubeFile implements SingleCubeFile {
    protected String path;


    protected AbstractCubeFile(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public File createFile() {
        return new File(getPath());
    }
}