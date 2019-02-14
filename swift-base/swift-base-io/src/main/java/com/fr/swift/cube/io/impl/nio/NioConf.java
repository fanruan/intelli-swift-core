package com.fr.swift.cube.io.impl.nio;

/**
 * @author anchore
 * @date 2018/7/22
 */
public class NioConf {
    private static final int BUF_SIZE = 12;

    private static final int FILE_SIZE = 22;

    private final String path;

    private final IoType ioType;

    private final int bufSize;

    private final int fileSize;

    private final boolean mapped;

    public NioConf(String path, IoType ioType) {
        this(path, ioType, BUF_SIZE, FILE_SIZE, false);
    }

    public NioConf(String path, IoType ioType, int bufSize, int fileSize, boolean mapped) {
        this.path = path;
        this.ioType = ioType;
        this.bufSize = bufSize;
        this.fileSize = fileSize;
        this.mapped = mapped;
    }

    public NioConf ofAnotherPath(String path) {
        return new NioConf(path, ioType, bufSize, fileSize, mapped);
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

    public int getBufSize() {
        return bufSize;
    }

    public int getFileSize() {
        return fileSize;
    }

    public boolean isMapped() {
        return mapped;
    }

    public enum IoType {
        //
        READ, APPEND, OVERWRITE
    }

}