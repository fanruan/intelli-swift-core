package com.fr.swift.executor.task.netty.protocol.attribute;


import com.fr.swift.executor.task.netty.protocol.session.Session;
import io.netty.util.AttributeKey;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public interface Attributes {

    AttributeKey<Session> SESSION = AttributeKey.newInstance("session");

}
