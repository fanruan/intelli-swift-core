package com.fr.swift.io.nio;

/**
 * @author anchore
 * @date 2018/7/22
 */
public class NioConf {
    private static final int PAGE_SIZE = 22;

    private final String path;

    private final IoType ioType;

    private final int pageSize;

    private final boolean mapped;

    public NioConf(String path, IoType ioType) {
        this(path, ioType, PAGE_SIZE, false);
    }

    public NioConf(String path, IoType ioType, int pageSize, boolean mapped) {
        this.path = path;
        this.ioType = ioType;
        this.pageSize = pageSize;
        this.mapped = mapped;
    }

    public NioConf ofAnotherPath(String path) {
        return new NioConf(path, ioType, pageSize, mapped);
    }

    public String getPath() {
        return path;
    }

    public IoType getIoType() {
        return ioType;
    }

    public boolean isRead() {
        return ioType == IoType.READ;
    }

    public boolean isOverwrite() {
        return ioType == IoType.OVERWRITE;
    }

    public boolean isAppend() {
        return ioType == IoType.APPEND;
    }

    public boolean isWrite() {
        return !isRead();
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isMapped() {
        return mapped;
    }

    public enum IoType {
        //
        READ, APPEND, OVERWRITE
    }

}