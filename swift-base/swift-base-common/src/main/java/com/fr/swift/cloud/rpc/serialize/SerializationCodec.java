package com.fr.swift.cloud.rpc.serialize;

/**
 * @author Heng.J
 * @date 2021/7/30
 * @description 编解码器
 * @since swift-1.2.0
 */
public interface SerializationCodec {

    //H.J TODO: 2021/8/3 目前是先序列化后压缩，对应解压后反序列化，以应付高效传输， 后续如有需求可以尝试先压缩后序列化（流式）

    // 消息体长度
    int MESSAGE_LENGTH = 4;

    // 是否压缩
    int MESSAGE_ZIP_INFO = 1;
}
