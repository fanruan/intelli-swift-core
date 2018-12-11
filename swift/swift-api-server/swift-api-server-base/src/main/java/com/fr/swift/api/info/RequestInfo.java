package com.fr.swift.api.info;

import com.fr.swift.base.json.JsonBuilder;

/**
 * 预解析后的sql信息
 * @see JsonBuilder#writeJsonString(Object)
 * @author yee
 * @date 2018/11/16
 */
public interface RequestInfo<T> extends Accepter<T> {
    String getAuthCode();

    Request AUTH = new Request() {
        @Override
        public String name() {
            return "AUTH";
        }
    };

    <R extends Request> R getRequest();

    interface Request {
        String name();
    }
}
