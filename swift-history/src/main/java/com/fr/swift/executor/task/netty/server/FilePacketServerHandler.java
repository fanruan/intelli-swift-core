package com.fr.swift.executor.task.netty.server;


import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.log.SwiftLoggers;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
@ChannelHandler.Sharable
public class FilePacketServerHandler extends SimpleChannelInboundHandler<FilePacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FilePacket packet) throws Exception {
		File file = packet.getFile();
		SwiftLoggers.getLogger().info("receive file from client: " + file.getName());
		FileReceiveServerHandler.fileLength = file.length();
		FileReceiveServerHandler.outputStream = new FileOutputStream(
				new File("/Users/hoky/Work/fanruan/code/swift-gc-old/target/cubes/" + file.getName())
		);
		packet.setACK(packet.getACK() + 1);
		ctx.writeAndFlush(packet);
	}
}
