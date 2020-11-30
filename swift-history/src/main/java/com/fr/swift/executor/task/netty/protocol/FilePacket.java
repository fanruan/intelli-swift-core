package com.fr.swift.executor.task.netty.protocol;

import java.io.File;
import java.io.Serializable;

import static com.fr.swift.executor.task.netty.protocol.command.Command.FILE;

/**
 * @author Hoky
 * @date 2020/11/26
 */
public class FilePacket extends Packet implements Serializable {

    private File file;
    private int startPos;
    private byte[] bytes;
    private int endPos;
    private boolean isNext;

    public boolean isNext() {
        return isNext;
    }

    public void setNext(boolean next) {
        isNext = next;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    private String targetPath;

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    private boolean isEnd;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    @Override
    public Byte getCommand() {
        return FILE;
    }


}
