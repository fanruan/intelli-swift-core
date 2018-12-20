package com.fr.swift.api.info;

import com.fr.swift.base.json.JsonBuilder;

/**
 * basic interface of request info for api and jdbc
 * @see JsonBuilder#writeJsonString(Object)
 * @author yee
 * @date 2018/11/16
 * @see BaseRequestInfo
 */
public interface RequestInfo<T extends RequestParserVisitor> extends Accepter<T> {
    /**
     * any request should contains an auth code except auth request.
     *
     * @return return null if it's an auth request.
     * return auth code if it's other request.
     */
    String getAuthCode();

    Request AUTH = new Request() {
        @Override
        public String name() {
            return "AUTH";
        }
    };

    /**
     * get request type like <code>AUTH</code>,
     * <code>SQL</code>...
     *
     * @param <R> type extend Request
     * @return request type for this request
     * @see RequestInfo#AUTH
     * @see Request
     */
    <R extends Request> R getRequest();

    /**
     * basic interface for request type
     */
    interface Request {
        /**
         * return request type name
         *
         * @return request type name
         */
        String name();
    }
}
