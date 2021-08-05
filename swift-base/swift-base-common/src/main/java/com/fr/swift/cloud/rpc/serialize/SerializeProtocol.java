package com.fr.swift.cloud.rpc.serialize;

import java.util.Arrays;

/**
 * @author Heng.J
 * @date 2021/7/30
 * @description
 * @since swift-1.2.0
 */
public enum SerializeProtocol {

    JDK_SERIALIZE("jdk"),
    KRYO_SERIALIZE("kryo"),
    HESSIAN_SERIALIZE("hessian"),       // jdbc不提供使用
    PROTOSTUFF_SERIALIZE("protostuff"), // 暂不提供使用
    ;

    private final String protocol;

    SerializeProtocol(String protocol) {
        this.protocol = protocol;
    }

    public static SerializeProtocol getEnum(String protocol) {
        return Arrays.stream(SerializeProtocol.values())
                .filter(value -> value.protocol.equals(protocol))
                .findFirst()
                .orElse(JDK_SERIALIZE);
    }
}
