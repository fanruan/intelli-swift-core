package com.fr.swift.cloud.executor.task.netty.client;

import com.fr.swift.cloud.executor.task.netty.protocol.FilePacket;
import com.fr.swift.cloud.executor.task.netty.protocol.TransferState;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Hoky
 * @date 2020/12/19
 */
public class FileInfoMap {
    private final static ConcurrentHashMap<String, FileInfo> fileInfoCache = new ConcurrentHashMap<>();

    public void put(String uuid, FileInfo fileInfo) {
        fileInfoCache.put(uuid, fileInfo);
    }

    public FileInfo get(String uuid) {
        return fileInfoCache.getOrDefault(uuid, new FileInfo());
    }

    public int getByteRead(String uuid) {
        return fileInfoCache.get(uuid).getByteRead();
    }

    public void setByteRead(String uuid, int byteRead) {
        fileInfoCache.get(uuid).setByteRead(byteRead);
    }

    public Long getStart(String uuid) {
        return fileInfoCache.get(uuid).getStart();
    }

    public void setStart(String uuid, Long start) {
        fileInfoCache.get(uuid).setStart(start);
    }

    public int getLastLength(String uuid) {
        return fileInfoCache.get(uuid).getLastLength();
    }

    public void setLastLength(String uuid, int lastLength) {
        fileInfoCache.get(uuid).setLastLength(lastLength);
    }

    public long getSendLength(String uuid) {
        return fileInfoCache.get(uuid).getSendLength();
    }

    public void setSendLength(String uuid, long sendLength) {
        fileInfoCache.get(uuid).setSendLength(sendLength);
    }

    public RandomAccessFile getRandomAccessFile(String uuid) {
        return fileInfoCache.get(uuid).getRandomAccessFile();
    }

    public void setRandomAccessFile(String uuid, RandomAccessFile randomAccessFile) {
        fileInfoCache.get(uuid).setRandomAccessFile(randomAccessFile);
    }

    public FilePacket getFilePacket(String uuid) {
        return fileInfoCache.get(uuid).getFilePacket();
    }

    public void setFilePacket(String uuid, FilePacket filePacket) {
        fileInfoCache.get(uuid).setFilePacket(filePacket);
    }

    public boolean isTransferred(String uuid) {
        return fileInfoCache.get(uuid).isTransferred();
    }

    public synchronized void transferred(String uuid) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.transferred();
        fileInfoCache.put(uuid, fileInfo);
    }

    public void start(String uuid) {
        fileInfoCache.get(uuid).startTransfer();
    }

    public TransferState getTransferState(String uuid) {
        return fileInfoCache.get(uuid).getTransferState();
    }

    public synchronized List<FileInfo> getUnStarted() {
        List<FileInfo> unstartList = fileInfoCache.values().stream()
                .filter(fileInfo -> fileInfo.getTransferState() == TransferState.UNSTART)
                .collect(Collectors.toList());
        for (FileInfo fileInfo : unstartList) {
            fileInfo.activeTransfer();
            fileInfoCache.put(fileInfo.getFilePacket().getUuid(), fileInfo);
        }
        return unstartList;
    }

    private final static FileInfoMap fileInfoMap = new FileInfoMap();

    FileInfoMap() {
    }

    public static FileInfoMap getFileInfoMap() {
        return fileInfoMap;
    }
}
