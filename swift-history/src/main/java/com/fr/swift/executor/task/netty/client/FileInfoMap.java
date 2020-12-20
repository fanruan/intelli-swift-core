package com.fr.swift.executor.task.netty.client;

import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.executor.task.netty.protocol.TransferState;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Hoky
 * @date 2020/12/19
 */
public class FileInfoMap {
    private final static ConcurrentHashMap<String, FileInfo> fileInfoMap = new ConcurrentHashMap<>();

    public void put(String uuid, FileInfo fileInfo) {
        fileInfoMap.put(uuid, fileInfo);
    }

    public FileInfo get(String uuid) {
        return fileInfoMap.getOrDefault(uuid, new FileInfo());
    }

    public int getByteRead(String uuid) {
        return fileInfoMap.get(uuid).getByteRead();
    }

    public void setByteRead(String uuid, int byteRead) {
        fileInfoMap.get(uuid).setByteRead(byteRead);
    }

    public Long getStart(String uuid) {
        return fileInfoMap.get(uuid).getStart();
    }

    public void setStart(String uuid, Long start) {
        fileInfoMap.get(uuid).setStart(start);
    }

    public int getLastLength(String uuid) {
        return fileInfoMap.get(uuid).getLastLength();
    }

    public void setLastLength(String uuid, int lastLength) {
        fileInfoMap.get(uuid).setLastLength(lastLength);
    }

    public long getSendLength(String uuid) {
        return fileInfoMap.get(uuid).getSendLength();
    }

    public void setSendLength(String uuid, long sendLength) {
        fileInfoMap.get(uuid).setSendLength(sendLength);
    }

    public RandomAccessFile getRandomAccessFile(String uuid) {
        return fileInfoMap.get(uuid).getRandomAccessFile();
    }

    public void setRandomAccessFile(String uuid, RandomAccessFile randomAccessFile) {
        fileInfoMap.get(uuid).setRandomAccessFile(randomAccessFile);
    }

    public FilePacket getFilePacket(String uuid) {
        return fileInfoMap.get(uuid).getFilePacket();
    }

    public void setFilePacket(String uuid, FilePacket filePacket) {
        fileInfoMap.get(uuid).setFilePacket(filePacket);
    }

    public boolean isTransferred(String uuid) {
        return fileInfoMap.get(uuid).isTransferred();
    }

    public synchronized void transferred(String uuid) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.transferred();
        fileInfoMap.put(uuid, fileInfo);
    }

    public void start(String uuid) {
        fileInfoMap.get(uuid).startTransfer();
    }

    public TransferState getTransferState(String uuid) {
        return fileInfoMap.get(uuid).getTransferState();
    }

    public synchronized List<FileInfo> getUnStarted() {
        List<FileInfo> unstartList = fileInfoMap.values().stream()
                .filter(fileInfo -> fileInfo.getTransferState() == TransferState.UNSTART)
                .collect(Collectors.toList());
        for (FileInfo fileInfo : unstartList) {
            fileInfo.activeTransfer();
            fileInfoMap.put(fileInfo.getFilePacket().getUuid(), fileInfo);
        }
        return unstartList;
    }
}
