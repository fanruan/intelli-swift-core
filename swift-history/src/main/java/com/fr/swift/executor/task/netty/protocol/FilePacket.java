package com.fr.swift.executor.task.netty.protocol;


import java.io.File;

import static com.fr.swift.executor.task.netty.protocol.command.Command.FILE_PACKET;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class FilePacket extends Packet {

    File file;

    int ACK;

    @Override
    public Byte getCommand() {
        return FILE_PACKET;
    }

    public FilePacket() {
    }

    public FilePacket(File file) {
        this.file = file;
    }

    public FilePacket(File file, int ACK) {
        this.file = file;
        this.ACK = ACK;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getACK() {
        return ACK;
    }

    public void setACK(int ACK) {
        this.ACK = ACK;
    }
}