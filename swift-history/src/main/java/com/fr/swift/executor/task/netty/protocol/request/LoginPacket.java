package com.fr.swift.executor.task.netty.protocol.request;


import com.fr.swift.executor.task.netty.protocol.Packet;

import static com.fr.swift.executor.task.netty.protocol.command.Command.LOGIN_PACKET_REQUEST;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class LoginPacket extends Packet {

    String name;

    String id;

    @Override
    public Byte getCommand() {
        return LOGIN_PACKET_REQUEST;
    }

    public LoginPacket() {
    }

    public LoginPacket(String name) {
        this.name = name;
    }

    public LoginPacket(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
