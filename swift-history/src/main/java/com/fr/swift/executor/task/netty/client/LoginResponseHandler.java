package com.fr.swift.executor.task.netty.client;

import com.fr.swift.executor.task.netty.protocol.response.LoginResponsePacket;
import com.fr.swift.log.SwiftLoggers;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
@ChannelHandler.Sharable
public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket packet) throws Exception {
        SwiftLoggers.getLogger().info(new Date() + " " + packet.getId() + " " + packet.getName() + " Successful login");
    }
}
