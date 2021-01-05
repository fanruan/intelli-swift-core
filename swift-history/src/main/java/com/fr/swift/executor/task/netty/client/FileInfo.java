package com.fr.swift.executor.task.netty.client;

import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.executor.task.netty.protocol.TransferState;

import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Hoky
 * @date 2020/12/19
 */
public class FileInfo {

    private int byteRead;
    private volatile Long start = 0l;   //使用Long 当传输的文件大于2G时，Integer类型会不够表达文件的长度
    private volatile int lastLength = 0;
    private volatile long sendLength = 0L;
    public RandomAccessFile randomAccessFile;
    private FilePacket filePacket;
    private AtomicBoolean isTransferred;
    private TransferState transferState;

    public FileInfo() {
        isTransferred = new AtomicBoolean(false);
        transferState = TransferState.UNSTART;
    }

    public int getByteRead() {
        return byteRead;
    }

    public void setByteRead(int byteRead) {
        this.byteRead = byteRead;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public int getLastLength() {
        return lastLength;
    }

    public void setLastLength(int lastLength) {
        this.lastLength = lastLength;
    }

    public long getSendLength() {
        return sendLength;
    }

    public void setSendLength(long sendLength) {
        this.sendLength = sendLength;
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }

    public void setRandomAccessFile(RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }

    public FilePacket getFilePacket() {
        return filePacket;
    }

    public void setFilePacket(FilePacket filePacket) {
        this.filePacket = filePacket;
    }

    public boolean isTransferred() {
        return isTransferred.get();
    }

    public void transferred() {
        isTransferred.set(true);
    }

    public void startTransfer() {
        transferState = TransferState.STARTED;
    }

    public void activeTransfer() {
        transferState = TransferState.ACTIVE;
    }

    public TransferState getTransferState() {
        return transferState;
    }

    public void memoryInfo(long sendLength, int byteRead, RandomAccessFile randomAccessFile, int lastLength, FilePacket filePacket) {
        this.setLastLength(lastLength);
        this.setSendLength(sendLength);
        this.setByteRead(byteRead);
        this.setRandomAccessFile(randomAccessFile);
        this.setFilePacket(filePacket);
    }

}
