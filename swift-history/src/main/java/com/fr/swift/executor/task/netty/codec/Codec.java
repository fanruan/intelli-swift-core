package com.fr.swift.executor.task.netty.codec;


import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.executor.task.netty.protocol.Packet;
import com.fr.swift.executor.task.netty.protocol.request.LoginPacket;
import com.fr.swift.executor.task.netty.protocol.response.LoginResponsePacket;
import com.fr.swift.executor.task.netty.protocol.serilizer.Serilizer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import static com.fr.swift.executor.task.netty.protocol.command.Command.FILE_PACKET;
import static com.fr.swift.executor.task.netty.protocol.command.Command.LOGIN_PACKET_REQUEST;
import static com.fr.swift.executor.task.netty.protocol.command.Command.LOGIN_PACKET_RESPONSE;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class Codec {

    public static final int TYPE = 0x12345678;

    private final Map<Byte, Class<? extends Packet>> packetTypeMap;

    public static Codec INSTANCE = new Codec();

    private Codec() {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(FILE_PACKET, FilePacket.class);
        packetTypeMap.put(LOGIN_PACKET_REQUEST, LoginPacket.class);
        packetTypeMap.put(LOGIN_PACKET_RESPONSE, LoginResponsePacket.class);
    }

    public void encode(ByteBuf byteBuf, Packet packet) {
        byte[] bytes = Serilizer.DEFAULT.serilize(packet);
        byteBuf.writeInt(TYPE);
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        // return byteBuf;
    }

    public Packet decode(ByteBuf byteBuf) {
        byteBuf.readInt();
        Byte command = byteBuf.readByte();
        int len = byteBuf.readInt();
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);

        Class clazz = packetTypeMap.get(command);
        if (clazz == null) {
            throw new NullPointerException("Parsing failed! No packets of this type");
        }

        return (Packet) Serilizer.DEFAULT.deSerilize(bytes, clazz);

    }


}
