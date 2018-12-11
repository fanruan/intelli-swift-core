package com.fr.swift.api.info;

import com.fr.swift.api.json.JsonRequestBuilder;

/**
 * 预解析后的sql信息
 *
 * @author yee
 * @date 2018/11/16
 * @see JsonRequestBuilder#buildRequest(RequestInfo)
 */
public interface RequestInfo<T> extends Accepter<T> {
    String getAuthCode();

    Request getRequest();

    Request AUTH = new Request() {
        @Override
        public String toString() {
            return "AUTH";
        }
    };

    interface Request {
    }
}
