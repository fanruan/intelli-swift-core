package com.fr.swift.cloud.rpc.handler;

import com.fr.swift.cloud.rpc.compress.CompressMode;
import io.netty.channel.ChannelPipeline;

/**
 * @author Heng.J
 * @date 2021/7/30
 * @description
 * @since swift-1.2.0
 */
public interface RpcMessageHandler {

    void handle(CompressMode compressMode, ChannelPipeline pipeline);
}

