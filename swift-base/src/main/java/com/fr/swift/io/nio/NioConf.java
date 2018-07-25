package com.fr.swift.io.nio;

/**
 * @author anchore
 * @date 2018/7/22
 */
public class NioConf {
    private static final int PAGE_SIZE = 22;

    private final String path;

    private final boolean write;

    private final int pageSize;

    private final boolean mapped;

    public NioConf(String path, boolean write) {
        this(path, write, PAGE_SIZE, false);
    }

    public NioConf(String path, boolean write, int pageSize, boolean mapped) {
        this.path = path;
        this.write = write;
        this.pageSize = pageSize;
        this.mapped = mapped;
    }

    public NioConf ofAnotherPath(String path) {
        return new NioConf(path, write, pageSize, mapped);
    }

    public String getPath() {
        return path;
    }

    public boolean isWrite() {
        return write;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isMapped() {
        return mapped;
    }

}