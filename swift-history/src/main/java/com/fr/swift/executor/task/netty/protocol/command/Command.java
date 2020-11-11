package com.fr.swift.executor.task.netty.protocol.command;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public interface Command {

    Byte FILE_PACKET = 1;

    Byte LOGIN_PACKET_REQUEST = 2;

    Byte LOGIN_PACKET_RESPONSE = 3;

}
