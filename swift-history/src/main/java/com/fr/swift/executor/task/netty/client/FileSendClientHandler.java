package com.fr.swift.executor.task.netty.client;


import com.fr.swift.executor.task.job.impl.MigrateJob;
import com.fr.swift.executor.task.netty.codec.Codec;
import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.executor.task.netty.protocol.Packet;
import com.fr.swift.log.SwiftLoggers;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;

import java.io.File;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
@ChannelHandler.Sharable
public class FileSendClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		int type = byteBuf.getInt(0);
		if (type == Codec.TYPE) {
			Packet packet = Codec.INSTANCE.decode(byteBuf);
			if (packet instanceof FilePacket) {
				FilePacket filePacket = (FilePacket) packet;
				if (filePacket.getACK() != 0) {
					writeAndFlushFileRegion(ctx, filePacket);
				} else {
					super.channelRead(ctx, packet);
				}
			} else {
				super.channelRead(ctx, packet);
			}
		} else {
			SwiftLoggers.getLogger().error("can not recognize this file");
		}
	}

	private void writeAndFlushFileRegion(ChannelHandlerContext ctx, FilePacket packet) {
		File file = packet.getFile();
		DefaultFileRegion fileRegion = new DefaultFileRegion(file, 0, file.length());
		ctx.writeAndFlush(fileRegion).addListener(future -> {
			if (future.isSuccess()) {
				SwiftLoggers.getLogger().info("sending file finished!");
				MigrateJob.countDown();
			}
		});
	}


}
