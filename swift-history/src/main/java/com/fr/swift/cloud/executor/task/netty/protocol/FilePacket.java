package com.fr.swift.cloud.executor.task.netty.protocol;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

import static com.fr.swift.cloud.executor.task.netty.protocol.command.Command.FILE;

/**
 * @author Hoky
 * @date 2020/11/26
 */
public class FilePacket extends Packet implements Serializable {

    private String uuid;
    private File file;
    private int startPos;
    private byte[] bytes;
    private int endPos;
    private boolean isFirst;

    public FilePacket() {
        this.uuid = UUID.randomUUID().toString();
    }

    public boolean isFirst() {
        return isFirst;
    }

    public FilePacket setFirst(boolean first) {
        isFirst = first;
        return this;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public FilePacket setTargetPath(String targetPath) {
        this.targetPath = targetPath;
        return this;
    }

    private String targetPath;

    public boolean isEnd() {
        return isEnd;
    }

    public FilePacket setEnd(boolean end) {
        isEnd = end;
        return this;
    }

    private boolean isEnd;

    public File getFile() {
        return file;
    }

    public FilePacket setFile(File file) {
        this.file = file;
        return this;
    }

    public int getStartPos() {
        return startPos;
    }

    public FilePacket setStartPos(int startPos) {
        this.startPos = startPos;
        return this;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public FilePacket setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    public int getEndPos() {
        return endPos;
    }

    public FilePacket setEndPos(int endPos) {
        this.endPos = endPos;
        return this;
    }

    @Override
    public Byte getCommand() {
        return FILE;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


}
