package com.fr.swift.executor.task.netty.protocol.response;


import com.fr.swift.executor.task.netty.protocol.Packet;
import com.fr.swift.executor.task.netty.protocol.command.Command;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class LoginResponsePacket extends Packet {

    String id;
    String name;

    public LoginResponsePacket() {
    }

    public LoginResponsePacket(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Byte getCommand() {
        return Command.LOGIN_PACKET_RESPONSE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
