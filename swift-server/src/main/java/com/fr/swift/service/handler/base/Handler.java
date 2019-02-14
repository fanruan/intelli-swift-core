package com.fr.swift.service.handler.base;

import com.fr.swift.event.base.SwiftRpcEvent;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/6/8
 */
public interface Handler<T extends SwiftRpcEvent> {
    <S extends Serializable> S handle(T event) throws Exception;
}
