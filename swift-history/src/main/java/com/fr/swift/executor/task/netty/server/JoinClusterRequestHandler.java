package com.fr.swift.executor.task.netty.server;


import com.fr.swift.executor.task.netty.protocol.request.LoginPacket;
import com.fr.swift.executor.task.netty.protocol.response.LoginResponsePacket;
import com.fr.swift.executor.task.netty.protocol.session.Session;
import com.fr.swift.executor.task.util.IDUtil;
import com.fr.swift.executor.task.util.SessionUtil;
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
public class JoinClusterRequestHandler extends SimpleChannelInboundHandler<LoginPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginPacket loginPacket) throws Exception {
        String id = IDUtil.randomId();
        SwiftLoggers.getLogger().info(new Date() + " [" + loginPacket.getName() + "-" + id + "] join in cluster");
        SessionUtil.bindSession(new Session(id, loginPacket.getName()), ctx.channel());

        ctx.writeAndFlush(new LoginResponsePacket(id, loginPacket.getName()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionUtil.unBindSession(ctx.channel());
    }
}
